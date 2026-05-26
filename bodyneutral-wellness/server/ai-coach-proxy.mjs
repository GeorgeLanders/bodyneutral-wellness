import http from "node:http";

const port = Number(process.env.PORT || 8787);
const model = process.env.OPENAI_MODEL || "gpt-4.1-mini";

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

async function createCoachReply({ message, profile, style }) {
  const apiKey = process.env.OPENAI_API_KEY;
  if (!apiKey) {
    throw new Error("OPENAI_API_KEY is not set on the proxy server.");
  }

  const response = await fetch("https://api.openai.com/v1/responses", {
    method: "POST",
    headers: {
      "Authorization": `Bearer ${apiKey}`,
      "Content-Type": "application/json"
    },
    body: JSON.stringify({
      model,
      instructions: [
        "You are an empathetic body-neutral wellness coach.",
        "Weight management may be discussed as an optional user goal, but never with shame, extreme dieting, or guaranteed outcomes.",
        "Emphasize sustainable habits: nourishment, hydration, sleep, stress care, movement, consistency, and medical-professional support when needed.",
        "Do not diagnose, prescribe, or make medical claims.",
        "Keep replies concise, practical, inclusive, and emotionally steady.",
        "For crisis, self-harm, medical emergency, or immediate danger, tell the user to contact local emergency services or a trusted person now.",
        style || ""
      ].join("\n"),
      input: [
        {
          role: "user",
          content: [
            {
              type: "input_text",
              text: JSON.stringify({ message, profile })
            }
          ]
        }
      ],
      max_output_tokens: 220
    })
  });

  const data = await response.json();
  if (!response.ok) {
    throw new Error(data?.error?.message || `OpenAI API error ${response.status}`);
  }

  return data.output_text || data.output?.flatMap((item) => item.content || [])
    .map((content) => content.text || "")
    .join("")
    .trim();
}

const server = http.createServer(async (req, res) => {
  if (req.method === "OPTIONS") {
    sendJson(res, 204, {});
    return;
  }

  if (req.method !== "POST" || req.url !== "/ai-coach") {
    sendJson(res, 404, { error: "Use POST /ai-coach" });
    return;
  }

  try {
    const payload = await readJson(req);
    if (!payload.message || typeof payload.message !== "string") {
      sendJson(res, 400, { error: "Missing string field: message" });
      return;
    }

    const reply = await createCoachReply(payload);
    sendJson(res, 200, { reply });
  } catch (error) {
    sendJson(res, 500, { error: error.message || "AI coach proxy failed." });
  }
});

server.listen(port, () => {
  console.log(`AI coach proxy listening on http://localhost:${port}/ai-coach`);
});
