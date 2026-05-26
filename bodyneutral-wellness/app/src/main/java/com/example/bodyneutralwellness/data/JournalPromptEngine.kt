package com.example.bodyneutralwellness.data

object JournalPromptEngine {
    fun promptFor(snapshot: WellnessSnapshot): String {
        return when {
            snapshot.gentleMode || snapshot.selectedMoodLabel in setOf("Low", "Cozy") ->
                "What is one neutral, non-critical thing you can say about your body right now?"
            snapshot.sleepHours in 1..5 ->
                "What would feel like real rest today, even if sleep was imperfect?"
            snapshot.nourishmentCount == 0 || snapshot.hydrationCups < 3 ->
                "What kind of basic care would help your body feel a little more supported?"
            snapshot.movementMinutes >= 10 ->
                "How did movement feel in your body today, without judging the amount?"
            snapshot.dailyIntention.isNotBlank() ->
                "How did \"${snapshot.dailyIntention}\" show up for you today?"
            snapshot.checkedHabitCount == snapshot.totalHabitCount && snapshot.totalHabitCount > 0 ->
                "What small act of care are you willing to actually notice and receive?"
            else ->
                "What did your body help you experience today?"
        }
    }
}
