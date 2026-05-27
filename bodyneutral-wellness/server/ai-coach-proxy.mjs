import http from "node:http";

const port = Number(process.env.PORT || 8787);
const provider = (process.env.AI_PROVIDER || "openai").toLowerCase();
const openaiModel = process.env.OPENAI_MODEL || "gpt-4o-mini";
const opencodeBaseUrl = (process.env.OPENCODE_BASE_URL || "https://opencode.ai/zen/v1").replace(/\/+$/, "");
const opencodeModel = process.env.OPENCODE_MODEL || "big-pickle";

const ipRequests = new Map();
setInterval(() => ipRequests.clear(), 60000);

function readJson(req) {
  return new Promise((resolve, reject) => {
    let body = "";
    req.on("data", (chunk) => {
      body += chunk;
      if (body.length > 100_000) {
        reject(new Error("Request body too large"));
        req.destroy();
      }
    });
    req.on("end", () => {
      try {
        resolve(body ? JSON.parse(body) : {});
      } catch {
        reject(new Error("Invalid JSON"));
      }
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

function buildInstructions(style) {
  return [
    "You are an empathetic body-neutral wellness coach.",
    "Weight management may be discussed as an optional user goal, but never with shame, extreme dieting, or guaranteed outcomes.",
    "Emphasize sustainable habits: nourishment, hydration, sleep, stress care, movement, consistency, and medical-professional support when needed.",
    "Do not diagnose, prescribe, or make medical claims.",
    "Keep replies concise, practical, inclusive, and emotionally steady.",
    "For crisis, self-harm, medical emergency, or immediate danger, tell the user to contact local emergency services or a trusted person now.",
    style || ""
  ]
    .filter(Boolean)
    .join("\n");
}

async function createCoachReplyOpenAIResponses({ message, profile, style }) {
  const apiKey = process.env.OPENAI_API_KEY;
  if (!apiKey) throw new Error("OPENAI_API_KEY is not set on the proxy server.");

  const response = await fetch("https://api.openai.com/v1/chat/completions", {
    method: "POST",
    headers: {
      "Authorization": `Bearer ${apiKey}`,
      "Content-Type": "application/json"
    },
    body: JSON.stringify({
      model: openaiModel,
      messages: [
        { role: "system", content: buildInstructions(style) },
        { role: "user", content: JSON.stringify({ message, profile }) }
      ],
      max_tokens: 220
    })
  });

  const data = await response.json();
  if (!response.ok) {
    throw new Error(data?.error?.message || `OpenAI API error ${response.status}`);
  }

  const content = data?.choices?.[0]?.message?.content;
  if (typeof content !== "string" || !content.trim()) {
    throw new Error("Unexpected OpenAI response format (missing choices[0].message.content).");
  }
  return content.trim();
}

async function createCoachReplyOpenCodeZen({ message, profile, style }) {
  const apiKey = process.env.OPENCODE_API_KEY;
  if (!apiKey) throw new Error("OPENCODE_API_KEY is not set on the proxy server.");

  const response = await fetch(`${opencodeBaseUrl}/chat/completions`, {
    method: "POST",
    headers: {
      "Authorization": `Bearer ${apiKey}`,
      "Content-Type": "application/json"
    },
    body: JSON.stringify({
      model: opencodeModel,
      messages: [
        { role: "system", content: buildInstructions(style) },
        { role: "user", content: JSON.stringify({ message, profile }) }
      ],
      max_tokens: 220
    })
  });

  const data = await response.json();
  if (!response.ok) {
    throw new Error(data?.error?.message || `OpenCode Zen API error ${response.status}`);
  }

  const content = data?.choices?.[0]?.message?.content;
  if (typeof content !== "string" || !content.trim()) {
    throw new Error(`Unexpected Zen response format (missing choices[0].message.content): ${JSON.stringify(data)}`);
  }
  return content.trim();
}

const server = http.createServer(async (req, res) => {
  if (req.method === "OPTIONS") {
    sendJson(res, 204, {});
    return;
  }

  const pathname = req.url.split("?")[0].replace(/\/+$/, "");
  if (req.method === "GET" && (pathname === "" || pathname === "/ai-coach")) {
    sendJson(res, 200, { status: "online", message: "Body-Neutral AI Coach Proxy is running!" });
    return;
  }

  if (req.method !== "POST") {
    sendJson(res, 404, { error: "Use POST /ai-coach" });
    return;
  }
  if (pathname !== "/ai-coach" && pathname !== "") {
    sendJson(res, 404, { error: "Use POST /ai-coach" });
    return;
  }

  const clientIp = req.headers["x-forwarded-for"]?.split(",")[0] || req.socket.remoteAddress;
  const count = ipRequests.get(clientIp) || 0;
  if (count >= 5) {
    sendJson(res, 429, { error: "Too many requests. Please wait a minute." });
    return;
  }
  ipRequests.set(clientIp, count + 1);

  const expectedSecret = process.env.APP_SECRET;
  if (expectedSecret && req.headers["x-app-secret"] !== expectedSecret) {
    sendJson(res, 403, { error: "Forbidden: Invalid app secret" });
    return;
  }

  try {
    const payload = await readJson(req);
    if (!payload.message || typeof payload.message !== "string") {
      sendJson(res, 400, { error: "Missing string field: message" });
      return;
    }

    const reply = provider === "opencode"
      ? await createCoachReplyOpenCodeZen(payload)
      : await createCoachReplyOpenAIResponses(payload);
    sendJson(res, 200, { reply });
  } catch (error) {
    console.error("AI coach proxy failed:", error);
    sendJson(res, 500, { error: error.message || "AI coach proxy failed." });
  }
});

server.listen(port, () => {
  console.log(`AI coach proxy listening on http://localhost:${port}/ai-coach (provider=${provider})`);
});
