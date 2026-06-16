import http from "node:http";

const port = Number(process.env.PORT || 8787);

// ── Multi-provider config (ported from freellmapi router) ──
// Priority-ordered list of providers. Each has a model, env key, and base URL.
// The router tries them in order, skipping unavailable/rate-limited ones.
const PROVIDERS = [
  {
    id: "opencode",
    envKey: "OPENCODE_API_KEY",
    baseUrl: (process.env.OPENCODE_BASE_URL || "https://opencode.ai/zen/v1").replace(/\/+$/, ""),
    model: process.env.OPENCODE_MODEL || "big-pickle",
    chatPath: "/chat/completions",
  },
  {
    id: "openai",
    envKey: "OPENAI_API_KEY",
    baseUrl: "https://api.openai.com/v1",
    model: process.env.OPENAI_MODEL || "gpt-4.1-mini",
    chatPath: "/responses",
  },
];

// ── Rate-limit penalty tracking (ported from freellmapi) ──
// Key: providerId → { count, lastHit, penalty }
const rateLimitPenalties = new Map();
const PENALTY_PER_429 = 3;
const MAX_PENALTY = 10;
const DECAY_INTERVAL_MS = 2 * 60 * 1000;
const DECAY_AMOUNT = 1;

function recordRateLimitHit(providerId) {
  const existing = rateLimitPenalties.get(providerId);
  const now = Date.now();
  if (existing) {
    existing.count++;
    existing.lastHit = now;
    existing.penalty = Math.min(existing.penalty + PENALTY_PER_429, MAX_PENALTY);
  } else {
    rateLimitPenalties.set(providerId, { count: 1, lastHit: now, penalty: PENALTY_PER_429 });
  }
}

function recordSuccess(providerId) {
  const existing = rateLimitPenalties.get(providerId);
  if (existing) {
    existing.penalty = Math.max(0, existing.penalty - 1);
    if (existing.penalty === 0) rateLimitPenalties.delete(providerId);
  }
}

function getPenalty(providerId) {
  const entry = rateLimitPenalties.get(providerId);
  if (!entry) return 0;
  const now = Date.now();
  const elapsed = now - entry.lastHit;
  const decaySteps = Math.floor(elapsed / DECAY_INTERVAL_MS);
  if (decaySteps > 0) {
    entry.penalty = Math.max(0, entry.penalty - decaySteps * DECAY_AMOUNT);
    entry.lastHit = now;
    if (entry.penalty === 0) { rateLimitPenalties.delete(providerId); return 0; }
  }
  return entry.penalty;
}

// ── Cooldown tracking ──
const cooldowns = new Map();
const COOLDOWN_MS = 60_000; // 1 minute cooldown after 429

function isOnCooldown(providerId) {
  const until = cooldowns.get(providerId);
  if (!until) return false;
  if (Date.now() > until) { cooldowns.delete(providerId); return false; }
  return true;
}

function setCooldown(providerId) {
  cooldowns.set(providerId, Date.now() + COOLDOWN_MS);
}

// ── Sticky session (ported from freellmapi) ──
// Remembers the last successful provider per conversation so Lumina
// doesn't "change personality" mid-chat.
const stickySessions = new Map();
const STICKY_TTL_MS = 30 * 60 * 1000; // 30 minutes

function getStickyProvider(conversationId) {
  if (!conversationId) return null;
  const entry = stickySessions.get(conversationId);
  if (!entry) return null;
  if (Date.now() > entry.expires) { stickySessions.delete(conversationId); return null; }
  return entry.providerId;
}

function setStickyProvider(conversationId, providerId) {
  if (!conversationId) return;
  stickySessions.set(conversationId, { providerId, expires: Date.now() + STICKY_TTL_MS });
}

// ── Helpers ──
function readJson(req) {
  return new Promise((resolve, reject) => {
    let body = "";
    req.on("data", (chunk) => {
      body += chunk;
      if (body.length > 100_000) { reject(new Error("Request body too large")); req.destroy(); }
    });
    req.on("end", () => {
      try { resolve(body ? JSON.parse(body) : {}); } catch { reject(new Error("Invalid JSON")); }
    });
    req.on("error", reject);
  });
}

function sendJson(res, status, payload) {
  res.writeHead(status, {
    "Content-Type": "application/json",
    "Access-Control-Allow-Origin": "*",
    "Access-Control-Allow-Methods": "POST, OPTIONS",
    "Access-Control-Allow-Headers": "Content-Type"
  });
  res.end(JSON.stringify(payload));
}

function buildCoachInstructions(style) {
  return [
    "You are an empathetic body-neutral wellness coach.",
    "Weight management may be discussed as an optional user goal, but never with shame, extreme dieting, or guaranteed outcomes.",
    "Emphasize sustainable habits: nourishment, hydration, sleep, stress care, movement, consistency, and medical-professional support when needed.",
    "Do not diagnose, prescribe, or make medical claims.",
    "Keep replies concise, practical, inclusive, and emotionally steady.",
    "For crisis, self-harm, medical emergency, or immediate danger, tell the user to contact local emergency services or a trusted person now.",
    style || ""
  ].filter(Boolean).join("\n");
}

function buildPrompt(action, payload) {
  const { message, profile, context } = payload;
  const p = profile || {};
  const c = context || {};

  switch (action) {
    case "recommend":
      return [
        "You are a body-neutral wellness recommendation engine. Based on the user's profile and daily snapshot, suggest ONE gentle action.",
        "Return ONLY valid JSON with these fields: { \"title\": string, \"reason\": string, \"actionLabel\": string, \"destination\": string }.",
        "destination must be one of: Breathing, Journal, Movement, Nourish, Sos, WellnessWins.",
        "Use body-neutral, shame-free language. No calorie talk, no body criticism.",
        "Keep title under 60 chars, reason under 120 chars, actionLabel under 30 chars.",
        "",
        `Profile: goals=${p.goals || "none"}, mobility=${p.mobilityPreference || "any"}, intention="${p.dailyIntention || ""}"`,
        `Snapshot: mood="${p.selectedMoodLabel || "ok"}", gentleMode=${p.gentleMode || false}, sleep=${p.sleepHours || "?"}h, movement=${p.movementMinutes || 0}min, hydration=${p.hydrationCups || 0}cups, nourish=${p.nourishmentCount || 0}, streak=${p.streakCount || 0}`,
        `Context: recent destination="${c.lastDestination || "none"}", feedback="${c.lastFeedback || "none"}"`,
        `Message from user: "${message || "suggest something"}"`
      ].join("\n");

    case "journal_prompt":
      return [
        "You are a body-neutral journal prompt generator. Based on the user's current state, suggest ONE reflective question.",
        "Return ONLY a single sentence prompt string, no additional text.",
        "The prompt should invite reflection without pressure. Use body-neutral language.",
        "Never ask about weight, calories, or appearance. Focus on feelings, sensations, and experiences.",
        "",
        `Profile: mood="${p.selectedMoodLabel || "ok"}", gentleMode=${p.gentleMode || false}`,
        `Snapshot: sleep=${p.sleepHours || "?"}h, intention="${p.dailyIntention || ""}", journalCount=${p.journalEntriesCount || 0}`,
        `Context: last prompt was "${c.lastPrompt || "none"}"`
      ].join("\n");

    case "weekly_reflection":
      return [
        "You are a body-neutral weekly reflection generator. Based on the user's week summary, create a kind reflection.",
        "Return ONLY valid JSON with these fields: { \"title\": string, \"body\": string, \"focus\": string }.",
        "title: under 40 chars. body: 1-2 sentences of reflection. focus: one short sentence suggesting next focus.",
        "Use warm, shame-free language. Do not mention weight or appearance.",
        "",
        `Week summary: streak=${p.streakCount || 0} days, gentleMode=${p.gentleMode || false}`,
        `Averages: sleep=${p.avgSleep || "?"}h, movement=${p.avgMovement || 0}min, hydration=${p.avgHydration || 0}cups`,
        `Care signals: journalEntries=${p.journalEntriesCount || 0}, nourish=${p.nourishmentCount || 0}, habitsChecked=${p.checkedHabitCount || 0}/${p.totalHabitCount || 0}`
      ].join("\n");

    case "nourish_insight":
      return [
        "You are a body-neutral nourishment insight generator. Based on the user's savoring logs, give a brief insight.",
        "Return ONLY a short paragraph (1-3 sentences). No JSON formatting.",
        "Focus on patterns around satisfaction, hunger type, and sensory experience (taste, texture, smell).",
        "Use supportive, shame-free language. No diet talk, no calorie talk, no weight talk.",
        "",
        `Logs: count=${c.logCount || 0}, avgSatisfaction=${c.avgSatisfaction || "?"}/5, commonHunger="${c.commonHunger || "mixed"}"`,
        `Sensory favorite: ${c.sensoryFavorite || "none"}`,
        `Hydration: ${p.hydrationCups || 0} cups today`
      ].join("\n");

    case "sos_grounding":
      return [
        "You are a compassionate grounding guide for someone in distress. Do NOT diagnose or give medical advice.",
        "Return ONLY a short grounding walkthrough (2-4 short paragraphs). No JSON.",
        "Use the 5-4-3-2-1 sensory grounding technique or gentle breathing guidance.",
        "If the user mentions self-harm, crisis, or emergency, tell them to contact local emergency services immediately.",
        "Use warm, steady, simple language. Speak directly to the user.",
        "",
        `User context: "${message || "feeling distressed"}"`,
        `Profile: mood="${p.selectedMoodLabel || "distressed"}", gentleMode=${p.gentleMode || false}`
      ].join("\n");

    case "insight":
      return [
        "You are a gentle wellness insight generator for a daily dashboard. Based on the user's snapshot, give one short encouraging observation.",
        "Return ONLY a single sentence (under 200 chars). No JSON, no formatting.",
        "Notice something positive or offer a kind observation. Do NOT mention weight, calories, or appearance.",
        "Be warm and brief, like a supportive friend checking in.",
        "",
        `Mood: "${p.selectedMoodLabel || "ok"}", gentleMode=${p.gentleMode || false}`,
        `Today: sleep=${p.sleepHours || "?"}h, movement=${p.movementMinutes || 0}min, hydration=${p.hydrationCups || 0}cups, nourish=${p.nourishmentCount || 0}`,
        `Streak: ${p.streakCount || 0} days, intention: "${p.dailyIntention || ""}"`
      ].join("\n");

    case "daily_plan":
      return [
        "You are a body-neutral daily planner. Based on the user's snapshot, suggest a simple daily plan.",
        "Return ONLY valid JSON with these fields: { \"movement\": string, \"nourish\": string, \"mindset\": string }.",
        "Keep suggestions very gentle, achievable, and body-neutral.",
        "",
        `Profile: goals=${p.goals || "none"}, mobility=${p.mobilityPreference || "any"}`,
        `Snapshot: mood="${p.selectedMoodLabel || "ok"}", gentleMode=${p.gentleMode || false}, streak=${p.streakCount || 0}`
      ].join("\n");

    case "weekly_deep_dive":
      return [
        "You are a body-neutral wellness analyzer. Review the user's week and provide a deep dive summary.",
        "Return ONLY a paragraph (3-4 sentences). Do NOT use JSON formatting.",
        "Highlight patterns of care, consistency, and softness. Do not mention weight.",
        "",
        `Week summary: streak=${p.streakCount || 0} days, gentleMode=${p.gentleMode || false}`,
        `Averages: sleep=${p.avgSleep || "?"}h, movement=${p.avgMovement || 0}min, hydration=${p.avgHydration || 0}cups`,
        `Logs: ${c.logsSummary || "none"}`
      ].join("\n");

    case "generate_flow":
      return [
        "You are a gentle movement instructor. Generate a custom movement flow sequence.",
        "Return ONLY a JSON array of strings, each representing a movement name. E.g., [\"Neck Stretch\", \"Seated Twist\"].",
        "Keep it to 3-5 movements appropriate for their mobility preference.",
        "",
        `Request: "${message || "suggest a flow"}"`,
        `Mobility: ${p.mobilityPreference || "any"}`
      ].join("\n");

    default:
      return buildCoachInstructions(payload.style || "");
  }
}

// ── Provider call with fallback ──
async function callProvider(provider, instructions, message, profile) {
  const apiKey = process.env[provider.envKey];
  if (!apiKey) throw new Error(`${provider.envKey} not set`);

  const url = `${provider.baseUrl}${provider.chatPath}`;

  let body;
  if (provider.id === "openai") {
    body = JSON.stringify({
      model: provider.model,
      instructions,
      input: [{ role: "user", content: [{ type: "input_text", text: JSON.stringify({ message, profile }) }] }],
      max_output_tokens: 300
    });
  } else {
    // OpenAI-compatible chat completions (OpenCode, etc.)
    body = JSON.stringify({
      model: provider.model,
      messages: [
        { role: "system", content: instructions },
        { role: "user", content: JSON.stringify({ message, profile }) }
      ],
      max_tokens: 300
    });
  }

  const response = await fetch(url, {
    method: "POST",
    headers: { "Authorization": `Bearer ${apiKey}`, "Content-Type": "application/json" },
    body
  });

  if (response.status === 429) {
    recordRateLimitHit(provider.id);
    setCooldown(provider.id);
    throw new Error(`RATE_LIMITED:${provider.id}`);
  }

  const data = await response.json();
  if (!response.ok) {
    throw new Error(data?.error?.message || `${provider.id} API error ${response.status}`);
  }

  let content;
  if (provider.id === "openai") {
    content = data.output_text || data.output?.flatMap((item) => item.content || [])
      .map((c) => c.text || "").join("").trim();
  } else {
    content = data?.choices?.[0]?.message?.content;
  }

  if (typeof content !== "string" || !content.trim()) {
    throw new Error(`Unexpected response format from ${provider.id}`);
  }

  recordSuccess(provider.id);
  return content.trim();
}

// ── Main AI reply with multi-provider fallback ──
async function createAIReply({ action, message, profile, style, context, conversationId }) {
  const instructions = buildPrompt(action || "coach", { message, profile, context });

  // Build ordered provider list: sticky session first, then by penalty
  const sticky = getStickyProvider(conversationId);
  const ordered = [...PROVIDERS].sort((a, b) => {
    if (a.id === sticky) return -1;
    if (b.id === sticky) return 1;
    return getPenalty(a.id) - getPenalty(b.id);
  });

  const errors = [];
  for (const provider of ordered) {
    if (isOnCooldown(provider.id)) continue;
    if (!process.env[provider.envKey]) continue;

    try {
      const reply = await callProvider(provider, instructions, message, profile);
      setStickyProvider(conversationId, provider.id);
      return { reply, provider: provider.id };
    } catch (err) {
      if (err.message?.startsWith("RATE_LIMITED:")) {
        errors.push(`${provider.id}: rate limited`);
        continue; // try next provider
      }
      errors.push(`${provider.id}: ${err.message}`);
      // For non-429 errors, also try next provider
      continue;
    }
  }

  // Fallback brain when all keys are missing/rate-limited
  console.log(`Using server-side fallback for action: ${action || "coach"}`);
  let fallbackReply = "";
  switch (action) {
    case "recommend":
      fallbackReply = JSON.stringify({
        title: "Gentle Stretching",
        reason: "A few moments to release muscle tension.",
        actionLabel: "Start Stretch",
        destination: "Breathing"
      });
      break;
    case "journal_prompt":
      fallbackReply = "What is one small thing your body did for you today that you appreciate?";
      break;
    case "weekly_reflection":
      fallbackReply = JSON.stringify({
        title: "A Week of Care",
        body: "You've shown consistency in listening to your body.",
        focus: "Gentle hydration and sleep."
      });
      break;
    case "nourish_insight":
      fallbackReply = "Savoring meals helps connect mind and body. Enjoy each texture.";
      break;
    case "sos_grounding":
      fallbackReply = "Breathe in slowly for 4 seconds, hold for 4, and release for 6. You are safe here.";
      break;
    case "insight":
      fallbackReply = "You are taking intentional steps to care for yourself today.";
      break;
    case "daily_plan":
      fallbackReply = JSON.stringify({
        movement: "5 minutes of neck rolls and shoulder shrugs",
        nourish: "A warm, comforting cup of tea",
        mindset: "Remind yourself: I am enough as I am today"
      });
      break;
    case "weekly_deep_dive":
      fallbackReply = "This week was centered on pausing when needed. Listening to physical signals is a key sign of progress.";
      break;
    case "generate_flow":
      fallbackReply = JSON.stringify(["Gentle Neck Stretch", "Mindful Breathing", "Seated Arm Circles"]);
      break;
    default: // coach
      const lower = (message || "").toLowerCase().trim();
      let res = "";
      if (lower.includes("tired") || lower.includes("exhausted") || lower.includes("fatigue") || lower.includes("exhaustion")) {
        res = "Rest is not laziness. It is restoration. Your body deserves space to recharge. Try one slow breath, then choose the smallest next step.";
      } else if (lower.includes("bad") || lower.includes("ugly") || lower.includes("hate") || lower.includes("body-image") || lower.includes("body image") || lower.includes("distress")) {
        res = "I hear you, and those feelings are real and heavy. Your worth is independent of how you view your body right now. Let us practice neutral breathing: you exist, and that is enough.";
      } else if (lower.includes("exercise") || lower.includes("workout") || lower.includes("move") || lower.includes("stretch")) {
        res = "Movement can be appreciation, not punishment. A few gentle minutes count. Try something seated or slow, and stop before it becomes pressure.";
      } else if (lower.includes("lose weight") || lower.includes("weight loss") || lower.includes("weight management")) {
        res = "Weight management can be supported without shame. Focus on repeatable habits first: nourishment, hydration, sleep, stress care, and movement you can return to.";
      } else if (lower.includes("eat") || lower.includes("food") || lower.includes("diet") || lower.includes("hungry") || lower.includes("nourish")) {
        res = "Food is care, not a moral test. Try checking in with hunger, comfort, taste, texture, and satisfaction without scoring yourself.";
      } else if (lower.includes("sleep") || lower.includes("insomnia") || lower.includes("rest")) {
        res = "Sleep and rest help your body repair. If sleep is hard, aim for a lower bar: dim light, unclenched jaw, and a few slow exhales.";
      } else if (lower.includes("sad") || lower.includes("depressed") || lower.includes("anxious") || lower.includes("stress") || lower.includes("anxiety") || lower.includes("panic")) {
        res = "You are safe here. Take one long, slow breath. If panic is building, name five things you can see and let the SOS tools anchor you.";
      } else if (lower.includes("happy") || lower.includes("good") || lower.includes("great") || lower.includes("amazing")) {
        res = "Let yourself enjoy this feeling. Noticing good moments is a real wellness practice, and it deserves a little room.";
      } else {
        res = "Thank you for sharing that with me. Your feelings are valid. What would feel most supportive right now: breathing, journaling, nourishment, movement, or rest?";
      }
      const prefix = style === "Empathic Rest" ? "🌸 [Empathic Rest Coach] " :
                     style === "Body Neutrality" ? "🛡️ [Body Neutrality Coach] " :
                     style === "Active Listener" ? "👂 [Active Listener Coach] " :
                     style === "Encourager" ? "✨ [Encourager Coach] " : "";
      fallbackReply = prefix + res;
      break;
  }

  return { reply: fallbackReply, provider: "fallback" };
}

// ── Rate limiting ──
const rateLimits = new Map();
const RATE_LIMIT_WINDOW_MS = 60_000;
const MAX_REQUESTS_PER_WINDOW = 20;

function checkRateLimit(ip) {
  const now = Date.now();
  const record = rateLimits.get(ip) || { count: 0, resetTime: now + RATE_LIMIT_WINDOW_MS };
  if (now > record.resetTime) { record.count = 1; record.resetTime = now + RATE_LIMIT_WINDOW_MS; }
  else { record.count++; }
  rateLimits.set(ip, record);
  return record.count <= MAX_REQUESTS_PER_WINDOW;
}

// ── Server ──
const server = http.createServer(async (req, res) => {
  if (req.method === "OPTIONS") { sendJson(res, 204, {}); return; }
  if (req.method !== "POST") { sendJson(res, 404, { error: "Use POST /ai-coach" }); return; }

  const pathname = req.url.split("?")[0].replace(/\/+$/, "");
  if (pathname !== "/ai-coach") { sendJson(res, 404, { error: "Use POST /ai-coach" }); return; }

  const clientIp = req.socket.remoteAddress;
  if (!checkRateLimit(clientIp)) {
    sendJson(res, 429, { error: "Rate limit exceeded. Please try again later." });
    return;
  }

  try {
    const payload = await readJson(req);
    if (!payload.message || typeof payload.message !== "string") {
      sendJson(res, 400, { error: "Missing string field: message" });
      return;
    }

    const { reply, provider: usedProvider } = await createAIReply(payload);
    sendJson(res, 200, { reply, provider: usedProvider });
  } catch (error) {
    sendJson(res, 500, { error: error.message || "AI coach proxy failed." });
  }
});

server.listen(port, () => {
  const activeProviders = PROVIDERS.filter(p => process.env[p.envKey]).map(p => p.id);
  console.log(`AI coach proxy listening on http://localhost:${port}/ai-coach`);
  console.log(`Active providers: ${activeProviders.join(", ") || "NONE — set OPENAI_API_KEY or OPENCODE_API_KEY"}`);
  console.log(`Features: multi-provider fallback, rate-limit penalties, sticky sessions`);
});
