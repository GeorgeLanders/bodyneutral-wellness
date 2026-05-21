# Habit Check-In Implementation Plan

> **For Antigravity:** REQUIRED WORKFLOW: Use `.agent/workflows/execute-plan.md` to execute this plan in single-flow mode.

**Goal:** Build the Thrive Together welcome screen and first daily habit check-in flow with local persistence.

**Architecture:** A Flutter MaterialApp with route-based navigation. The app starts on a welcome screen, then navigates to a daily check-in screen that saves the user’s completion state locally.

**Tech Stack:** Flutter, Dart, Shared Preferences, flutter_test.

---

### Task 1: Add app shell and routing

**Files:**
- Modify: `lib/main.dart`
- Create: `lib/app.dart`
- Create: `lib/screens/welcome_screen.dart`
- Create: `lib/screens/daily_checkin_screen.dart`
- Test: `test/widget/app_navigation_test.dart`

**Step 1: Write the failing test**

```dart
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:thrive_together/app.dart';

void main() {
  testWidgets('App starts at welcome screen', (tester) async {
    await tester.pumpWidget(const ThriveTogetherApp());

    expect(find.text('Welcome to Thrive Together'), findsOneWidget);
    expect(find.text('Start your first check-in'), findsOneWidget);
  });
}
```

**Step 2: Run test to verify it fails**

Run: `flutter test test/widget/app_navigation_test.dart`
Expected: FAIL because `lib/app.dart` / `ThriveTogetherApp` or expected strings are missing.

**Step 3: Implement minimal app shell**

- Update `lib/main.dart` to import `app.dart` and launch `ThriveTogetherApp`.
- Create `lib/app.dart` with `MaterialApp`, theme, and `home: const WelcomeScreen()`.
- Define named routes for `/welcome` and `/checkin`.

**Minimal navigation example:**

```dart
import 'package:flutter/material.dart';
import 'screens/welcome_screen.dart';
import 'screens/daily_checkin_screen.dart';

class ThriveTogetherApp extends StatelessWidget {
  const ThriveTogetherApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Thrive Together',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.blue),
        useMaterial3: true,
      ),
      initialRoute: '/welcome',
      routes: {
        '/welcome': (context) => const WelcomeScreen(),
        '/checkin': (context) => const DailyCheckInScreen(),
      },
    );
  }
}
```

**Step 4: Run the test again**

Run: `flutter test test/widget/app_navigation_test.dart`
Expected: PASS.

**Step 5: Commit**

```bash
git add lib/main.dart lib/app.dart lib/screens/welcome_screen.dart lib/screens/daily_checkin_screen.dart test/widget/app_navigation_test.dart
git commit -m "feat: add app shell and welcome/check-in routing"
```

---

### Task 2: Build the welcome screen UI

**Files:**
- Modify: `lib/screens/welcome_screen.dart`
- Test: `test/widget/welcome_screen_test.dart`

**Step 1: Write the failing test**

```dart
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:thrive_together/screens/welcome_screen.dart';

void main() {
  testWidgets('Welcome screen shows headline and CTA', (tester) async {
    await tester.pumpWidget(const MaterialApp(home: WelcomeScreen()));

    expect(find.text('Welcome to Thrive Together'), findsOneWidget);
    expect(find.text('A judgment-free space for habits that help you feel stronger, calmer, and more confident'), findsOneWidget);
    expect(find.text('Start your first check-in'), findsOneWidget);
  });
}
```

**Step 2: Run test to verify it fails**

Run: `flutter test test/widget/welcome_screen_test.dart`
Expected: FAIL because the welcome screen UI is missing or incomplete.

**Step 3: Implement the welcome screen**

- Build a `Scaffold` with a centered column.
- Add the headline, supporting copy, and a primary button.
- Use a soft background color or gradient.
- On button tap, navigate to `/checkin`.

**Step 4: Run the test again**

Run: `flutter test test/widget/welcome_screen_test.dart`
Expected: PASS.

**Step 5: Commit**

```bash
git add lib/screens/welcome_screen.dart test/widget/welcome_screen_test.dart
git commit -m "feat: implement welcome screen UI"
```

---

### Task 3: Implement daily check-in UI and local model

**Files:**
- Create: `lib/models/daily_checkin.dart`
- Create: `lib/services/storage_service.dart`
- Modify: `lib/screens/daily_checkin_screen.dart`
- Test: `test/unit/storage_service_test.dart`
- Test: `test/widget/daily_checkin_screen_test.dart`
- Modify: `pubspec.yaml`

**Step 1: Add shared_preferences dependency**

Update `pubspec.yaml`:

```yaml
dependencies:
  flutter:
    sdk: flutter
  cupertino_icons: ^1.0.0
  shared_preferences: ^2.0.0
```

Then run: `flutter pub get`

**Step 2: Write storage service tests**

Create a test for `StorageService` that verifies saving and loading the daily check-in status.

```dart
import 'package:flutter_test/flutter_test.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:thrive_together/services/storage_service.dart';

void main() {
  test('save and load daily check-in status', () async {
    SharedPreferences.setMockInitialValues({});
    final service = StorageService();

    await service.saveCheckIn('2026-05-20', 'completed');
    final status = await service.loadCheckIn('2026-05-20');

    expect(status, 'completed');
  });
}
```

**Step 3: Run storage service test to verify it fails**

Run: `flutter test test/unit/storage_service_test.dart`
Expected: FAIL because `StorageService` does not exist.

**Step 4: Implement the storage service and model**

- `lib/models/daily_checkin.dart`: define `DailyCheckIn` with `date` and `status`.
- `lib/services/storage_service.dart`: implement `saveCheckIn(String date, String status)` and `loadCheckIn(String date)` using `SharedPreferences`.

**Step 5: Run storage service test again**

Run: `flutter test test/unit/storage_service_test.dart`
Expected: PASS.

**Step 6: Build the daily check-in screen UI**

- Show prompt text: `Today, I’ll choose one small wellbeing habit to complete`.
- Add two buttons: `I did it` and `I skipped`.
- On tap, call `StorageService().saveCheckIn(today, status)`.
- Display a success `SnackBar` or inline confirmation after tap.
- If desired, disable both buttons after a selection and show the chosen status.

**Step 7: Write the widget test**

```dart
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:thrive_together/screens/daily_checkin_screen.dart';

void main() {
  testWidgets('Daily check-in screen shows prompt and buttons', (tester) async {
    await tester.pumpWidget(const MaterialApp(home: DailyCheckInScreen()));

    expect(find.text('Today, I’ll choose one small wellbeing habit to complete'), findsOneWidget);
    expect(find.text('I did it'), findsOneWidget);
    expect(find.text('I skipped'), findsOneWidget);
  });
}
```

**Step 8: Run widget test**

Run: `flutter test test/widget/daily_checkin_screen_test.dart`
Expected: PASS.

**Step 9: Commit**

```bash
git add pubspec.yaml lib/models/daily_checkin.dart lib/services/storage_service.dart lib/screens/daily_checkin_screen.dart test/unit/storage_service_test.dart test/widget/daily_checkin_screen_test.dart
git commit -m "feat: add daily check-in model, storage, and UI"
```

---

### Task 4: Verify navigation and persistence

**Files:**
- Modify: `lib/screens/welcome_screen.dart`
- Modify: `lib/screens/daily_checkin_screen.dart`
- Test: `test/widget/navigation_and_persistence_test.dart`

**Step 1: Write a navigation test**

```dart
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:thrive_together/app.dart';

void main() {
  testWidgets('navigate from welcome to check-in', (tester) async {
    await tester.pumpWidget(const ThriveTogetherApp());

    expect(find.text('Welcome to Thrive Together'), findsOneWidget);
    await tester.tap(find.text('Start your first check-in'));
    await tester.pumpAndSettle();

    expect(find.text('Today, I’ll choose one small wellbeing habit to complete'), findsOneWidget);
  });
}
```

**Step 2: Run test**

Run: `flutter test test/widget/navigation_and_persistence_test.dart`
Expected: PASS.

**Step 3: Add persistence verification**

- Update the daily check-in screen to read `StorageService().loadCheckIn(today)` on init.
- If today’s status exists, show a confirmation state and keep selection buttons disabled.

**Step 4: Commit**

```bash
git add lib/screens/daily_checkin_screen.dart test/widget/navigation_and_persistence_test.dart
git commit -m "fix: verify navigation and persist daily check-in state"
```

---

### Task 5: Documentation and quality checks

**Files:**
- Modify: `docs/plans/2026-05-20-habit-checkin-implementation.md`
- Optional: `docs/plans/task.md`

**Step 1: Review the new screens in the emulator**

Run: `flutter run` and manually verify the welcome screen, button tap, and check-in flow.

**Step 2: Run full test suite**

Run: `flutter test`
Expected: PASS.

**Step 3: Update task tracker if desired**

Add a new row to `docs/plans/task.md` describing the habit check-in MVP tasks.

**Step 4: Final commit**

```bash
git add docs/plans/2026-05-20-habit-checkin-implementation.md docs/plans/task.md
git commit -m "docs: add habit check-in implementation plan"
```

---

Plan complete and saved to `docs/plans/2026-05-20-habit-checkin-implementation.md`.
Next step: run `.agent/workflows/execute-plan.md` to execute this plan task-by-task in single-flow mode.
