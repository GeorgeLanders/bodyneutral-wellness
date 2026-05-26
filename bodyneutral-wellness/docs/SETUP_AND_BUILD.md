# Setup And Build

## Requirements

- Windows.
- Android Studio.
- Android SDK.
- Java 17.
- Gradle wrapper included in the project.
- Android device or emulator.

## Open In Android Studio

1. Open Android Studio.
2. Select `C:\Users\George\Documents\Project\bodyneutral-wellness`.
3. Let Gradle sync.
4. Choose the `app` run configuration.
5. Select an emulator or device.
6. Run the app.

## Terminal Commands

From:

```powershell
C:\Users\George\Documents\Project\bodyneutral-wellness
```

Build debug APK:

```powershell
.\gradlew.bat assembleDebug
```

Run unit tests:

```powershell
.\gradlew.bat testDebugUnitTest
```

Compile Android tests:

```powershell
.\gradlew.bat compileDebugAndroidTestKotlin
```

Run Android lint:

```powershell
.\gradlew.bat lintDebug
```

Run connected Android UI tests:

```powershell
.\gradlew.bat connectedDebugAndroidTest
```

If more than one Android device is connected, target one device:

```powershell
$env:ANDROID_SERIAL = "DEVICE_ID"
.\gradlew.bat connectedDebugAndroidTest
```

## Cleaning

Clean only when needed, such as stale resources, old APK installs, or dex folder conflicts.

```powershell
.\gradlew.bat clean assembleDebug
```

If Gradle daemons hold files open:

```powershell
.\gradlew.bat --stop
```

Then rebuild:

```powershell
.\gradlew.bat assembleDebug
```

## SDK Warning

This warning can appear:

```text
SDK processing. This version only understands SDK XML versions up to 3 but an SDK XML file of version 4 was encountered.
```

It usually means Android Studio and command-line SDK tools are out of sync. Update these in Android Studio SDK Manager:

- Android SDK Command-line Tools.
- Android SDK Platform-Tools.
- Android SDK Build-Tools.

The warning is not fatal if builds pass.

## Java Time / Desugaring

The app uses `java.time` APIs with `minSdk = 24`. Core library desugaring is enabled in `app/build.gradle.kts`:

```kotlin
isCoreLibraryDesugaringEnabled = true
coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")
```
