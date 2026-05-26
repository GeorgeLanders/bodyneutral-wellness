package com.example.bodyneutralwellness.data

import junit.framework.TestCase.assertTrue
import org.junit.Test

class AchievementEngineTest {
    @Test
    fun badgesFor_matchingActivity_unlocksRelevantBadges() {
        val badges = AchievementEngine.badgesFor(
            streakCount = 3,
            journalEntriesCount = 3,
            nourishLogsCount = 0,
            movementMinutes = 12,
            hydrationCups = 4,
            checkedHabitCount = 3,
            gentleCheckInCount = 1
        )

        assertTrue(badges.first { it.title == "Returned to Care" }.unlocked)
        assertTrue(badges.first { it.title == "Reflection Keeper" }.unlocked)
        assertTrue(badges.first { it.title == "Movement Kindness" }.unlocked)
        assertTrue(badges.first { it.title == "Hydration Kindness" }.unlocked)
    }
}
