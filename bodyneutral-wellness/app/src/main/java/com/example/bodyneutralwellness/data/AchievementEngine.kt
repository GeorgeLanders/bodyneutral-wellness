package com.example.bodyneutralwellness.data

data class AchievementBadge(
    val icon: String,
    val title: String,
    val description: String,
    val unlocked: Boolean
)

object AchievementEngine {
    fun badgesFor(
        streakCount: Int,
        journalEntriesCount: Int,
        nourishLogsCount: Int,
        movementMinutes: Int,
        hydrationCups: Int,
        checkedHabitCount: Int,
        gentleCheckInCount: Int
    ): List<AchievementBadge> {
        return listOf(
            AchievementBadge("R", "Returned to Care", "Checked in for 3 days.", streakCount >= 3),
            AchievementBadge("J", "Reflection Keeper", "Saved 3 journal reflections.", journalEntriesCount >= 3),
            AchievementBadge("N", "Nourishment Noticed", "Logged 3 mindful savoring moments.", nourishLogsCount >= 3),
            AchievementBadge("M", "Movement Kindness", "Tracked at least 10 minutes of movement today.", movementMinutes >= 10),
            AchievementBadge("H", "Hydration Kindness", "Logged 4 cups of hydration today.", hydrationCups >= 4),
            AchievementBadge("C", "Care Completed", "Checked off at least 3 gentle habits today.", checkedHabitCount >= 3),
            AchievementBadge("S", "Rest Honored", "Used gentle mode or low-pressure check-ins.", gentleCheckInCount > 0)
        )
    }
}
