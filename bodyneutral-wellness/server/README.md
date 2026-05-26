# AI Coach Proxy

This tiny server keeps the OpenAI API key out of the Android app. The app sends wellness context to this proxy, and the proxy calls the OpenAI Responses API.

## Run

```powershell
$env:OPENAI_API_KEY = "your-rotated-server-side-key"
npm start
```

Then set the Android app's Settings > AI Coach Connection > Private proxy URL to:

```text
http://10.0.2.2:8787/ai-coach
```

Use `10.0.2.2` for the Android emulator to reach the host machine. For a physical device, use a reachable HTTPS endpoint instead.

Do not paste API keys into the Android app.
