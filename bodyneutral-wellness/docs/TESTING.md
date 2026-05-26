# Testing

## Current Health Check

The latest full scan passed:

- `testDebugUnitTest`
- `compileDebugAndroidTestKotlin`
- `lintDebug`
- `assembleDebug`
- `node --check server\ai-coach-proxy.mjs`
- `connectedDebugAndroidTest` on Samsung device `SM-S928W`

## Unit Tests

Unit tests live under:

```text
app/src/test/
```

Current coverage includes:

- Recommendation engine behavior.
- Recommendation feedback sorting.
- AI coach offline fallback.
- Journal prompt engine.
- Weekly reflection engine.
- Achievement engine.
- Nourish insights engine.
- Main screen ViewModel.

Run:

```powershell
.\gradlew.bat testDebugUnitTest
```

## Android Tests

Android tests live under:

```text
app/src/androidTest/
```

Current coverage includes:

- Main screen Compose content smoke test.

Compile:

```powershell
.\gradlew.bat compileDebugAndroidTestKotlin
```

Run on connected device:

```powershell
.\gradlew.bat connectedDebugAndroidTest
```

## Lint

Run:

```powershell
.\gradlew.bat lintDebug
```

Lint report:

```text
app/build/reports/lint-results-debug.html
```

## Manual QA Checklist

Before a release, manually test:

- Onboarding completes and stores preferences.
- Dashboard mood check-in saves and appears in the 7-day pattern.
- Gentle Day Mode changes recommendations.
- Recommendation Helpful / Not today buttons update learned feedback.
- What should I do now? opens the top recommended screen.
- Journal prompt refresh works.
- Journal entries save.
- Coach offline response works.
- Coach memory chips appear after messages.
- SOS chips appear after distress language.
- Nourish log saves and insights update.
- Hydration add/subtract works.
- Movement video playback works.
- Movement flow builder, timer, pause, skip, completion reflection work.
- Profile badges unlock as expected.
- Settings reset daily data.
- Settings erase all data.
- AI proxy URL can be saved.
- App survives rotation or backgrounding on key screens.

## Known Caveats

- The AI proxy must be deployed separately for live AI responses.
- Connected tests can fail if an emulator connection drops. Rerun against a specific physical device if needed.
- Some text contains emoji and expressive copy; visual QA should confirm no text clipping on small screens.
