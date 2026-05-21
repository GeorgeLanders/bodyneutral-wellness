# Thrive Together — Habit Check-In Design

**Date:** 2026-05-20
**Status:** Approved
**Platform:** Flutter
**Scope:** MVP welcome flow + daily habit check-in

## 1. Purpose

Deliver a lightweight, body-positive introduction to Thrive Together that helps users begin a daily habit check-in immediately after onboarding.

## 2. Goals

- Create a warm, supportive first impression
- Reinforce the app’s non-judgmental mission
- Make the first action simple: start a daily check-in
- Keep the initial experience low-friction and easy to complete

## 3. Core Experience

### Welcome screen

- Headline: `Welcome to Thrive Together`
- Supporting copy: `A judgment-free space for habits that help you feel stronger, calmer, and more confident`
- Visual treatment: soft nature-inspired illustration or warm gradient background
- Primary CTA: `Start your first check-in`
- Secondary reassurance: `No calorie counting. No pressure. Just small progress every day.`

### First check-in flow

- Immediately after the welcome screen, present the daily habit check-in screen
- Prompt text: `Today, I’ll choose one small wellbeing habit to complete`
- Simple binary response buttons: `I did it` / `I skipped`
- Positive, encouraging feedback when the user completes the habit
- Gentle, supportive messaging when the user skips

## 4. Data Model

- `daily_checkin` entry stored locally
- Fields:
  - `date`
  - `status` (`completed` or `skipped`)
  - `note` (optional encouragement or context)

## 5. Navigation Flow

- `WelcomeScreen` → `DailyCheckInScreen`
- After `DailyCheckInScreen` completion, optionally show a small success state and then return to the home/dashboard flow

## 6. Why this approach

- Respects Thrive Together’s body-positive philosophy
- Avoids overwhelming the user with onboarding questions or features
- Provides an immediate, tangible action that builds momentum
- Sets the stage for later habit tracking, streaks, and gentle progress insights

## 7. Next step

Write the implementation plan for the welcome + check-in MVP using the writing-plans process.
