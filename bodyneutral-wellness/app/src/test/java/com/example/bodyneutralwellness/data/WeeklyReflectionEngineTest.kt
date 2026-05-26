package com.example.bodyneutralwellness.data

import junit.framework.TestCase.assertTrue
import org.junit.Test

class WeeklyReflectionEngineTest {
    @Test
    fun reflectionFor_manyCareSignals_mentionsPattern() {
        val reflection = WeeklyReflectionEngine.reflectionFor(
            WellnessSnapshot(
                gentleMode = false,
                selectedMoodLabel = "Okay",
                streakCount = 4,
                hydrationCups = 5,
                movementMinutes = 12,
                nourishmentCount = 2,
                sleepHours = 7,
                journalEntriesCount = 2,
                checkedHabitCount = 2,
                totalHabitCount = 3,
                dailyIntention = "Self-Compassion"
            )
        )

        assertTrue(reflection.title.contains("Pattern"))
    }
}
