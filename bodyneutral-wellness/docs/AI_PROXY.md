# AI Proxy

The Android app must not contain an OpenAI API key. Mobile APKs can be inspected, so any key shipped in the app should be considered exposed.

The project includes a small proxy server in:

```text
server/
```

The Android app sends the user message and wellness context to the proxy. The proxy reads `OPENAI_API_KEY` from the server environment and calls the OpenAI Responses API.

## Files

```text
server/package.json
server/ai-coach-proxy.mjs
server/README.md
```

## Local Run

From:

```powershell
C:\Users\George\Documents\Project\bodyneutral-wellness\server
```

Set the key in the server environment:

```powershell
$env:OPENAI_API_KEY = "your-rotated-server-side-key"
npm start
```

The proxy listens on:

```text
http://localhost:8787/ai-coach
```

For Android emulator, set the app's Settings > AI Coach Connection > Private proxy URL to:

```text
http://10.0.2.2:8787/ai-coach
```

For a physical device, use a reachable HTTPS endpoint.

## Expected Request

The app sends JSON shaped like:

```json
{
  "message": "I feel anxious today",
  "profile": {
    "name": "Friend",
    "goals": "Supportive weight management without shame",
    "mobilityPreference": "mix",
    "dailyIntention": "Self-Compassion",
    "streakCount": 3,
    "hydrationCups": 4,
    "movementMinutes": 10,
    "nourishmentCount": 1,
    "sleepHours": 7
  },
  "style": "Body-neutral, compassionate..."
}
```

## Expected Response

The proxy returns one of:

```json
{ "reply": "..." }
```

The Android app also accepts `text` or `message` fields as fallbacks.

## Safety Rules

- Do not paste OpenAI API keys into the Android app.
- Do not commit API keys to git.
- Rotate any key that was pasted into chat or exposed.
- Use HTTPS for any deployed proxy.
- Add rate limiting before public release.
- Add authentication or app attestation before public release.
- Log as little user wellness text as possible.

## Current AI Behavior

If no proxy URL is configured, the app uses local offline support responses.

If a proxy URL is configured but unavailable, the app falls back to offline support and explains that the live AI service could not be reached.
