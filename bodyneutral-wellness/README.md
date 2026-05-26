# BodyNeutral Wellness

BodyNeutral Wellness is an Android wellness app for all bodies. It supports shame-free daily care through gentle movement, mindful nourishment, journaling, stress support, recommendations, and optional sustainable weight-management habits.

The app is built with Kotlin, Jetpack Compose, Material 3, Navigation 3, Media3 video playback, local SharedPreferences storage, and an optional AI coach proxy.

## Core Idea

Most wellness and fitness apps feel intense, generic, or shame-based. BodyNeutral Wellness is designed to feel calmer:

- No quick fixes.
- No body shaming.
- No weight-loss pressure.
- Support for all genders and body types.
- Optional weight-management support through sustainable habits.
- Local-first privacy where possible.

## Main Features

- Daily dashboard with mood check-in, Gentle Day Mode, weekly check-in pattern, recommendations, and weekly reflection.
- Personalized recommendation engine with local Helpful / Not today feedback.
- AI-style wellness coach with offline responses and optional secure proxy-backed AI.
- Smart SOS flow for anxiety, body-image distress, overwhelm, food panic, and sleeplessness.
- Body-neutral journal with context-aware reflection prompts.
- Mindful nourishment tracker, hydration support, savoring logs, and non-diet insights.
- Gentle movement video library and movement flow player.
- Habit tracker, streaks, wellness wins, and achievement badges.
- Community screen with inclusive sample posts.
- Settings for reminders, voice reader, AI proxy URL, data reset, and onboarding reset.

See [docs/FEATURES.md](docs/FEATURES.md) for the full feature inventory.

## Project Structure

```text
bodyneutral-wellness/
  app/                    Android app module
  app/src/main/java/      Kotlin + Compose source
  app/src/main/res/raw/   Bundled movement and mindfulness videos
  server/                 Optional AI coach proxy server
  docs/                   Project documentation
```

## Quick Start

Open the project in Android Studio, let Gradle sync, then run the `app` configuration on an emulator or Android device.

Terminal build:

```powershell
.\gradlew.bat assembleDebug
```

Run checks:

```powershell
.\gradlew.bat testDebugUnitTest
.\gradlew.bat compileDebugAndroidTestKotlin
.\gradlew.bat lintDebug
```

See [docs/SETUP_AND_BUILD.md](docs/SETUP_AND_BUILD.md) and [docs/TESTING.md](docs/TESTING.md).

## AI Coach Proxy

The Android app must not contain an OpenAI API key. Live AI responses should go through the proxy in `server/`, which reads `OPENAI_API_KEY` from the server environment.

See [docs/AI_PROXY.md](docs/AI_PROXY.md).

## Safety

This app provides general wellness support. It is not medical advice, mental health treatment, crisis care, eating-disorder treatment, or a guaranteed weight-loss program.

See [docs/PRIVACY_AND_SAFETY.md](docs/PRIVACY_AND_SAFETY.md).

## Play Store Path

The app is a strong prototype moving toward MVP. Before public launch, it needs visual QA, policy docs, closed testing, deployed backend infrastructure, store assets, and a privacy policy URL.

See [docs/PLAY_STORE_READINESS.md](docs/PLAY_STORE_READINESS.md).
