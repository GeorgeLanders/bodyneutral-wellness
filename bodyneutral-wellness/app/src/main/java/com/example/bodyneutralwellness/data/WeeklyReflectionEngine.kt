package com.example.bodyneutralwellness.data

data class WeeklyReflection(
    val title: String,
    val body: String,
    val focus: String
)

object WeeklyReflectionEngine {
    fun reflectionFor(snapshot: WellnessSnapshot): WeeklyReflection {
        val careSignals = listOf(
            snapshot.hydrationCups >= 4,
            snapshot.nourishmentCount > 0,
            snapshot.movementMinutes >= 10,
            snapshot.checkedHabitCount > 0,
            snapshot.journalEntriesCount > 0,
            snapshot.dailyIntention.isNotBlank()
        ).count { it }

        return when {
            snapshot.gentleMode || snapshot.selectedMoodLabel == "Low" ->
                WeeklyReflection(
                    title = "This Week's Gentle Note",
                    body = "Tender weeks still count. Your app history suggests the kindest next step is softness, not pressure.",
                    focus = "Keep choosing the smallest supportive action."
                )
            careSignals >= 4 ->
                WeeklyReflection(
                    title = "This Week's Pattern",
                    body = "You have several signs of care in motion: tracking, reflection, nourishment, movement, or intention.",
                    focus = "Let consistency feel calm instead of performative."
                )
            snapshot.streakCount >= 3 ->
                WeeklyReflection(
                    title = "This Week's Continuity",
                    body = "Your streak shows you have been returning to care, even if each day looked different.",
                    focus = "Notice the return, not perfection."
                )
            else ->
                WeeklyReflection(
                    title = "This Week's Invitation",
                    body = "There is room to begin lightly. One supportive action is enough to restart momentum.",
                    focus = "Pick one practice that feels easiest."
                )
        }
    }
}
