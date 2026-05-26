package com.example.bodyneutralwellness.data

import junit.framework.TestCase.assertTrue
import org.junit.Test

class JournalPromptEngineTest {
    @Test
    fun promptFor_gentleLowMood_usesBodyNeutralPrompt() {
        val prompt = JournalPromptEngine.promptFor(
            WellnessSnapshot(
                gentleMode = true,
                selectedMoodLabel = "Low",
                streakCount = 0,
                hydrationCups = 4,
                movementMinutes = 0,
                nourishmentCount = 1,
                sleepHours = 7,
                journalEntriesCount = 0,
                checkedHabitCount = 0,
                totalHabitCount = 3,
                dailyIntention = ""
            )
        )

        assertTrue(prompt.contains("neutral", ignoreCase = true))
    }
}
