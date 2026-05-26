package com.example.bodyneutralwellness.data

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

class RecommendationEngineTest {
    @Test
    fun recommendationsFor_gentleLowMood_prioritizesBreathingAndJournal() {
        val recommendations = RecommendationEngine.recommendationsFor(
            baseSnapshot(gentleMode = true, selectedMoodLabel = "Low")
        )

        assertEquals(RecommendationDestination.Breathing, recommendations[0].destination)
        assertTrue(recommendations.any { it.destination == RecommendationDestination.Journal })
    }

    @Test
    fun recommendationsFor_completeHabits_includesWins() {
        val recommendations = RecommendationEngine.recommendationsFor(
            baseSnapshot(checkedHabitCount = 3, totalHabitCount = 3)
        )

        assertTrue(recommendations.any { it.destination == RecommendationDestination.WellnessWins })
    }

    @Test
    fun recommendationsFor_dismissedDestination_movesItLower() {
        val recommendations = RecommendationEngine.recommendationsFor(
            baseSnapshot(
                gentleMode = true,
                selectedMoodLabel = "Low",
                feedback = mapOf(
                    RecommendationDestination.Breathing to RecommendationFeedback(dismissedCount = 3)
                )
            )
        )

        assertTrue(recommendations.first().destination != RecommendationDestination.Breathing)
    }

    private fun baseSnapshot(
        gentleMode: Boolean = false,
        selectedMoodLabel: String? = "Okay",
        checkedHabitCount: Int = 0,
        totalHabitCount: Int = 3,
        feedback: Map<RecommendationDestination, RecommendationFeedback> = emptyMap()
    ) = WellnessSnapshot(
        gentleMode = gentleMode,
        selectedMoodLabel = selectedMoodLabel,
        streakCount = 1,
        hydrationCups = 4,
        movementMinutes = 12,
        nourishmentCount = 1,
        sleepHours = 7,
        journalEntriesCount = 1,
        checkedHabitCount = checkedHabitCount,
        totalHabitCount = totalHabitCount,
        dailyIntention = "",
        recommendationFeedback = feedback
    )
}
