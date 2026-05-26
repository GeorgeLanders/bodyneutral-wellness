package com.example.bodyneutralwellness.data

enum class RecommendationDestination {
    Breathing,
    Journal,
    Movement,
    Nourish,
    Sos,
    WellnessWins
}

data class WellnessSnapshot(
    val gentleMode: Boolean,
    val selectedMoodLabel: String?,
    val streakCount: Int,
    val hydrationCups: Int,
    val movementMinutes: Int,
    val nourishmentCount: Int,
    val sleepHours: Int,
    val journalEntriesCount: Int,
    val checkedHabitCount: Int,
    val totalHabitCount: Int,
    val dailyIntention: String,
    val recommendationFeedback: Map<RecommendationDestination, RecommendationFeedback> = emptyMap()
)

data class WellnessRecommendation(
    val title: String,
    val reason: String,
    val actionLabel: String,
    val destination: RecommendationDestination,
    val feedback: RecommendationFeedback = RecommendationFeedback()
)

data class RecommendationFeedback(
    val helpfulCount: Int = 0,
    val dismissedCount: Int = 0
)

object RecommendationEngine {
    fun recommendationsFor(snapshot: WellnessSnapshot): List<WellnessRecommendation> {
        val recommendations = mutableListOf<WellnessRecommendation>()

        if (snapshot.gentleMode || snapshot.selectedMoodLabel in setOf("Low", "Cozy")) {
            recommendations += WellnessRecommendation(
                title = "Start with a softer reset",
                reason = "Gentle mode or a tender mood calls for low-pressure grounding first.",
                actionLabel = "Breathe",
                destination = RecommendationDestination.Breathing
            )
            recommendations += WellnessRecommendation(
                title = "Write one body-neutral sentence",
                reason = "A short journal check-in can make the feeling specific without making it bigger.",
                actionLabel = "Journal",
                destination = RecommendationDestination.Journal
            )
        }

        if (snapshot.selectedMoodLabel in setOf("Low", "Cozy") && snapshot.gentleMode) {
            recommendations += WellnessRecommendation(
                title = "Use the SOS grounding flow",
                reason = "When distress and gentle mode overlap, grounding support should be easy to reach.",
                actionLabel = "SOS",
                destination = RecommendationDestination.Sos
            )
        }

        if (snapshot.hydrationCups < 3 || snapshot.nourishmentCount == 0) {
            recommendations += WellnessRecommendation(
                title = "Check in with basic care",
                reason = "Your tracker suggests nourishment or hydration could use a kind little nudge.",
                actionLabel = "Nourish",
                destination = RecommendationDestination.Nourish
            )
        }

        if (!snapshot.gentleMode && snapshot.movementMinutes < 10 && snapshot.selectedMoodLabel != "Low") {
            recommendations += WellnessRecommendation(
                title = "Try a few minutes of easy movement",
                reason = "A short practice can count without turning care into a performance.",
                actionLabel = "Move",
                destination = RecommendationDestination.Movement
            )
        }

        if (snapshot.sleepHours in 1..5) {
            recommendations += WellnessRecommendation(
                title = "Protect your energy today",
                reason = "Low sleep is a good reason to choose the smallest helpful option.",
                actionLabel = "Breathe",
                destination = RecommendationDestination.Breathing
            )
        }

        if (snapshot.totalHabitCount > 0 && snapshot.checkedHabitCount == snapshot.totalHabitCount) {
            recommendations += WellnessRecommendation(
                title = "Notice the care you already gave",
                reason = "Your habits are checked off. Let the win land before adding more.",
                actionLabel = "View wins",
                destination = RecommendationDestination.WellnessWins
            )
        }

        if (snapshot.journalEntriesCount == 0 && snapshot.dailyIntention.isNotBlank()) {
            recommendations += WellnessRecommendation(
                title = "Reflect on your intention",
                reason = "\"${snapshot.dailyIntention}\" is already chosen. A quick note can anchor it.",
                actionLabel = "Journal",
                destination = RecommendationDestination.Journal
            )
        }

        if (recommendations.isEmpty()) {
            recommendations += WellnessRecommendation(
                title = "Choose one kind next step",
                reason = "Your check-in looks steady. Pick whichever practice feels easiest to begin.",
                actionLabel = "Breathe",
                destination = RecommendationDestination.Breathing
            )
        }

        return recommendations
            .distinctBy { it.title }
            .map { recommendation ->
                recommendation.copy(
                    feedback = snapshot.recommendationFeedback[recommendation.destination] ?: RecommendationFeedback()
                )
            }
            .sortedWith(
                compareBy<WellnessRecommendation> {
                    (it.feedback.dismissedCount - it.feedback.helpfulCount).coerceAtLeast(-3)
                }.thenBy { it.title }
            )
            .take(3)
    }
}
