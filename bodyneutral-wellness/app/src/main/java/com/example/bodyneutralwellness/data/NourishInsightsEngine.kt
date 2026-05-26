package com.example.bodyneutralwellness.data

data class NourishInsight(
    val title: String,
    val body: String
)

object NourishInsightsEngine {
    fun insightFor(logs: List<NourishLog>, hydrationCups: Int): NourishInsight {
        if (logs.isEmpty()) {
            return NourishInsight(
                title = "Notice Without Pressure",
                body = "Your savoring log is ready when you are. One meal, snack, or drink is enough to begin."
            )
        }

        val averageSatisfaction = logs.map { it.satisfaction }.average()
        val mostCommonHungerType = logs
            .groupingBy { it.hungerType }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key
            ?: "care"
        val sensoryFavorite = listOf(
            "taste" to logs.map { it.tasteRating }.average(),
            "texture" to logs.map { it.textureRating }.average(),
            "smell" to logs.map { it.smellRating }.average()
        ).maxBy { it.second }.first
        val hydrationNote = if (hydrationCups >= 4) {
            "Hydration is showing up as a steady support today."
        } else {
            "A few sips could be a kind next step."
        }

        return NourishInsight(
            title = "Your Nourish Pattern",
            body = "Recent logs lean toward $mostCommonHungerType, with $sensoryFavorite standing out. Satisfaction is around ${averageSatisfaction.toInt()} of 5. $hydrationNote"
        )
    }
}
