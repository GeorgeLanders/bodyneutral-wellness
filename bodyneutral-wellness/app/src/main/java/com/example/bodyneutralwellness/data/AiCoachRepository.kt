package com.example.bodyneutralwellness.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

data class AiCoachContext(
    val userName: String,
    val goals: Set<String>,
    val mobilityPreference: String,
    val dailyIntention: String,
    val streakCount: Int,
    val hydrationCups: Int,
    val movementMinutes: Int,
    val nourishmentCount: Int,
    val sleepHours: Int
)

class AiCoachRepository(
    private val proxyUrl: String
) {
    suspend fun generateReply(userText: String, context: AiCoachContext): String {
        val trimmedProxyUrl = proxyUrl.trim()
        if (trimmedProxyUrl.isBlank()) {
            return generateLocalReply(userText)
        }

        return runCatching {
            requestProxyReply(trimmedProxyUrl, userText, context)
        }.getOrElse {
            "${generateLocalReply(userText)}\n\nI could not reach the AI coach service just now, so I used the built-in support response."
        }
    }

    private suspend fun requestProxyReply(
        endpoint: String,
        userText: String,
        context: AiCoachContext
    ): String = withContext(Dispatchers.IO) {
        val connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 10_000
            readTimeout = 20_000
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("Accept", "application/json")
            doOutput = true
        }

        val payload = JSONObject().apply {
            put("message", userText)
            put("profile", JSONObject().apply {
                put("name", context.userName)
                put("goals", context.goals.joinToString(", "))
                put("mobilityPreference", context.mobilityPreference)
                put("dailyIntention", context.dailyIntention)
                put("streakCount", context.streakCount)
                put("hydrationCups", context.hydrationCups)
                put("movementMinutes", context.movementMinutes)
                put("nourishmentCount", context.nourishmentCount)
                put("sleepHours", context.sleepHours)
            })
            put(
                "style",
                "Body-neutral, compassionate, non-diagnostic wellness support. Keep replies concise, practical, and shame-free."
            )
        }

        OutputStreamWriter(connection.outputStream).use { writer ->
            writer.write(payload.toString())
        }

        val responseCode = connection.responseCode
        val stream = if (responseCode in 200..299) connection.inputStream else connection.errorStream
        val responseBody = stream.bufferedReader().use { it.readText() }
        connection.disconnect()

        if (responseCode !in 200..299) {
            error("AI proxy returned HTTP $responseCode")
        }

        val json = JSONObject(responseBody)
        json.optString("reply")
            .ifBlank { json.optString("text") }
            .ifBlank { json.optString("message") }
            .ifBlank { error("AI proxy response did not include reply, text, or message.") }
    }

    fun generateLocalReply(userText: String): String {
        val lower = userText.lowercase()
        return when {
            lower.contains("tired") || lower.contains("exhausted") || lower.contains("fatigue") || lower.contains("exhaustion") ->
                "Rest is not laziness. It is restoration. Your body deserves space to recharge. Try one slow breath, then choose the smallest next step."
            lower.contains("bad") || lower.contains("ugly") || lower.contains("hate") || lower.contains("body-image") || lower.contains("body image") || lower.contains("distress") ->
                "I hear you, and those feelings are real and heavy. Your worth is independent of how you view your body right now. Let us practice neutral breathing: you exist, and that is enough."
            lower.contains("exercise") || lower.contains("workout") || lower.contains("move") || lower.contains("stretch") ->
                "Movement can be appreciation, not punishment. A few gentle minutes count. Try something seated or slow, and stop before it becomes pressure."
            lower.contains("lose weight") || lower.contains("weight loss") || lower.contains("weight management") ->
                "Weight management can be supported without shame. Focus on repeatable habits first: nourishment, hydration, sleep, stress care, and movement you can return to."
            lower.contains("eat") || lower.contains("food") || lower.contains("diet") || lower.contains("hungry") || lower.contains("nourish") ->
                "Food is care, not a moral test. Try checking in with hunger, comfort, taste, texture, and satisfaction without scoring yourself."
            lower.contains("sleep") || lower.contains("insomnia") || lower.contains("rest") ->
                "Sleep and rest help your body repair. If sleep is hard, aim for a lower bar: dim light, unclenched jaw, and a few slow exhales."
            lower.contains("sad") || lower.contains("depressed") || lower.contains("anxious") || lower.contains("stress") || lower.contains("anxiety") || lower.contains("panic") ->
                "You are safe here. Take one long, slow breath. If panic is building, name five things you can see and let the SOS tools anchor you."
            lower.contains("happy") || lower.contains("good") || lower.contains("great") || lower.contains("amazing") ->
                "Let yourself enjoy this feeling. Noticing good moments is a real wellness practice, and it deserves a little room."
            else ->
                "Thank you for sharing that with me. Your feelings are valid. What would feel most supportive right now: breathing, journaling, nourishment, movement, or rest?"
        }
    }
}
