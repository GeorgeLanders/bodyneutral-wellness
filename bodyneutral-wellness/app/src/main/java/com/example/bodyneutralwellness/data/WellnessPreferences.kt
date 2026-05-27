package com.example.bodyneutralwellness.data

import android.content.Context
import android.content.SharedPreferences
import com.example.bodyneutralwellness.BuildConfig
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import org.json.JSONArray
import org.json.JSONObject

class WellnessPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("wellness_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_NAME = "user_name"
        private const val KEY_ONBOARDING_DONE = "onboarding_done"
        private const val KEY_GOALS = "wellness_goals"
        private const val KEY_MOBILITY = "mobility_preference"
        private const val KEY_AFFIRMATION = "custom_affirmation"
        private const val KEY_NOURISHMENT = "nourishment_count"
        private const val KEY_SLEEP = "sleep_hours"
        private const val KEY_MOVEMENT = "movement_minutes"
        private const val KEY_STREAK = "streak_count"
        private const val KEY_LAST_ACTIVE = "last_active_date"
        private const val KEY_TRACKER_DATE = "tracker_date"
        private const val KEY_JOURNAL = "journal_entries"
        private const val KEY_HYDRATION_CUPS = "hydration_cups"
        private const val KEY_DAILY_CALORIES = "daily_calories"
        private const val KEY_SHOW_CALORIES = "show_calories"
        private const val KEY_COMMUNITY_POSTS = "community_posts"
        private const val KEY_SOUND_SELECTION = "sound_selection"
        private const val KEY_TTS_RATE = "tts_rate"
        private const val KEY_TTS_PITCH = "tts_pitch"
        private const val KEY_REMINDER_HYDRATION = "reminder_hydration"
        private const val KEY_REMINDER_STRETCH = "reminder_stretch"
        private const val KEY_CUSTOM_BREATHS = "custom_breaths"
        private const val KEY_DAILY_INTENTION = "daily_intention"
        private const val KEY_AI_COACH_PROXY_URL = "ai_coach_proxy_url"
        private const val KEY_COACH_TOPICS = "coach_topics_v1"
        private const val KEY_RECOMMENDATION_FEEDBACK = "recommendation_feedback_v1"
    }

    // --- Onboarding ---
    var onboardingDone: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING_DONE, false)
        set(value) = prefs.edit().putBoolean(KEY_ONBOARDING_DONE, value).apply()

    // --- User Profile ---
    var userName: String
        get() = prefs.getString(KEY_NAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_NAME, value).apply()

    var wellnessGoals: Set<String>
        get() = prefs.getStringSet(KEY_GOALS, emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet(KEY_GOALS, value).apply()

    var mobilityPreference: String
        get() = prefs.getString(KEY_MOBILITY, "mix") ?: "mix"
        set(value) = prefs.edit().putString(KEY_MOBILITY, value).apply()

    var customAffirmation: String
        get() = prefs.getString(KEY_AFFIRMATION, "") ?: ""
        set(value) = prefs.edit().putString(KEY_AFFIRMATION, value).apply()

    // --- Daily Tracker ---
    private val today: String get() = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

    private fun ensureTodayTracker() {
        val savedDate = prefs.getString(KEY_TRACKER_DATE, "") ?: ""
        if (savedDate != today) {
            // New day: reset daily trackers, update streak
            updateStreak()
            prefs.edit()
                .putString(KEY_TRACKER_DATE, today)
                .putInt(KEY_NOURISHMENT, 0)
                .putInt(KEY_SLEEP, 0)
                .putInt(KEY_MOVEMENT, 0)
                .putInt(KEY_HYDRATION_CUPS, 0)
                .putInt(KEY_DAILY_CALORIES, 0)
                .apply()
        }
    }

    var nourishmentCount: Int
        get() { ensureTodayTracker(); return prefs.getInt(KEY_NOURISHMENT, 0) }
        set(value) { ensureTodayTracker(); prefs.edit().putInt(KEY_NOURISHMENT, value).apply() }

    var sleepHours: Int
        get() { ensureTodayTracker(); return prefs.getInt(KEY_SLEEP, 0) }
        set(value) { ensureTodayTracker(); prefs.edit().putInt(KEY_SLEEP, value).apply() }

    var movementMinutes: Int
        get() { ensureTodayTracker(); return prefs.getInt(KEY_MOVEMENT, 0) }
        set(value) { ensureTodayTracker(); prefs.edit().putInt(KEY_MOVEMENT, value).apply() }

    var hydrationCups: Int
        get() { ensureTodayTracker(); return prefs.getInt(KEY_HYDRATION_CUPS, 0) }
        set(value) { ensureTodayTracker(); prefs.edit().putInt(KEY_HYDRATION_CUPS, value).apply() }

    var dailyCalories: Int
        get() { ensureTodayTracker(); return prefs.getInt(KEY_DAILY_CALORIES, 0) }
        set(value) { ensureTodayTracker(); prefs.edit().putInt(KEY_DAILY_CALORIES, value).apply() }

    var showCaloriesSetting: Boolean
        get() = prefs.getBoolean(KEY_SHOW_CALORIES, true)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_CALORIES, value).apply()

    var soundSelection: String
        get() = prefs.getString(KEY_SOUND_SELECTION, "None") ?: "None"
        set(value) = prefs.edit().putString(KEY_SOUND_SELECTION, value).apply()

    var ttsRate: Float
        get() = prefs.getFloat(KEY_TTS_RATE, 1.0f)
        set(value) = prefs.edit().putFloat(KEY_TTS_RATE, value).apply()

    var ttsPitch: Float
        get() = prefs.getFloat(KEY_TTS_PITCH, 1.0f)
        set(value) = prefs.edit().putFloat(KEY_TTS_PITCH, value).apply()

    var reminderHydration: Boolean
        get() = prefs.getBoolean(KEY_REMINDER_HYDRATION, true)
        set(value) = prefs.edit().putBoolean(KEY_REMINDER_HYDRATION, value).apply()

    var reminderStretch: Boolean
        get() = prefs.getBoolean(KEY_REMINDER_STRETCH, true)
        set(value) = prefs.edit().putBoolean(KEY_REMINDER_STRETCH, value).apply()

    var aiCoachProxyUrl: String
        get() = prefs.getString(KEY_AI_COACH_PROXY_URL, null)
            ?: (if (BuildConfig.DEBUG) "http://10.0.2.2:8787/ai-coach" else "")
        set(value) = prefs.edit().putString(KEY_AI_COACH_PROXY_URL, value).apply()

    fun getCoachTopics(): List<String> {
        val json = prefs.getString(KEY_COACH_TOPICS, "[]") ?: "[]"
        val arr = JSONArray(json)
        return (0 until arr.length()).map { arr.getString(it) }
    }

    fun rememberCoachTopic(userText: String) {
        val topic = when {
            userText.contains("tired", true) || userText.contains("exhaust", true) -> "exhaustion"
            userText.contains("body", true) || userText.contains("ugly", true) || userText.contains("hate", true) -> "body image"
            userText.contains("anx", true) || userText.contains("panic", true) || userText.contains("stress", true) -> "stress"
            userText.contains("weight", true) || userText.contains("lose", true) -> "weight management"
            userText.contains("food", true) || userText.contains("eat", true) || userText.contains("nourish", true) -> "nourishment"
            userText.contains("sleep", true) || userText.contains("rest", true) -> "rest"
            userText.contains("move", true) || userText.contains("stretch", true) -> "movement"
            else -> "check-in"
        }
        val updated = (listOf(topic) + getCoachTopics().filterNot { it == topic }).take(4)
        prefs.edit().putString(KEY_COACH_TOPICS, JSONArray(updated).toString()).apply()
    }

    fun getRecommendationFeedback(): Map<RecommendationDestination, RecommendationFeedback> {
        val raw = prefs.getString(KEY_RECOMMENDATION_FEEDBACK, "{}") ?: "{}"
        val obj = JSONObject(raw)
        return RecommendationDestination.entries.associateWith { destination ->
            val item = obj.optJSONObject(destination.name)
            RecommendationFeedback(
                helpfulCount = item?.optInt("helpful", 0) ?: 0,
                dismissedCount = item?.optInt("dismissed", 0) ?: 0
            )
        }
    }

    fun recordRecommendationFeedback(destination: RecommendationDestination, helpful: Boolean) {
        val raw = prefs.getString(KEY_RECOMMENDATION_FEEDBACK, "{}") ?: "{}"
        val obj = JSONObject(raw)
        val item = obj.optJSONObject(destination.name) ?: JSONObject()
        val key = if (helpful) "helpful" else "dismissed"
        item.put(key, item.optInt(key, 0) + 1)
        obj.put(destination.name, item)
        prefs.edit().putString(KEY_RECOMMENDATION_FEEDBACK, obj.toString()).apply()
    }

    // --- Streak ---
    var streakCount: Int
        get() = prefs.getInt(KEY_STREAK, 0)
        private set(value) = prefs.edit().putInt(KEY_STREAK, value).apply()

    private fun updateStreak() {
        val lastActive = prefs.getString(KEY_LAST_ACTIVE, "") ?: ""
        val todayStr = today
        if (lastActive == todayStr) return // already updated today

        if (lastActive.isNotEmpty()) {
            val lastDate = LocalDate.parse(lastActive, DateTimeFormatter.ISO_LOCAL_DATE)
            val todayDate = LocalDate.now()
            val daysBetween = java.time.temporal.ChronoUnit.DAYS.between(lastDate, todayDate)
            if (daysBetween == 1L) {
                streakCount = streakCount + 1
            } else if (daysBetween > 1L) {
                streakCount = 1 // reset
            }
        } else {
            streakCount = 1 // first ever day
        }
        prefs.edit().putString(KEY_LAST_ACTIVE, todayStr).apply()
    }

    fun recordTodayActive() {
        val lastActive = prefs.getString(KEY_LAST_ACTIVE, "") ?: ""
        if (lastActive != today) {
            updateStreak()
        }
        prefs.edit().putString(KEY_LAST_ACTIVE, today).apply()
    }

    fun saveDailyCheckIn(dateStr: String, moodLabel: String, gentleMode: Boolean) {
        val entry = JSONObject().apply {
            put("mood", moodLabel)
            put("gentleMode", gentleMode)
        }
        prefs.edit().putString("daily_checkin_$dateStr", entry.toString()).apply()
        recordTodayActive()
    }

    fun getDailyCheckIn(dateStr: String): Pair<String, Boolean>? {
        val raw = prefs.getString("daily_checkin_$dateStr", null) ?: return null
        val obj = JSONObject(raw)
        return obj.optString("mood", "") to obj.optBoolean("gentleMode", false)
    }

    fun getRecentDailyCheckIns(days: Int): List<DailyCheckIn> {
        return (0 until days).map { offset ->
            val date = LocalDate.now().minusDays(offset.toLong())
            val dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
            val saved = getDailyCheckIn(dateStr)
            DailyCheckIn(
                date = dateStr,
                dayLabel = date.dayOfWeek.name.take(3),
                moodLabel = saved?.first.orEmpty(),
                gentleMode = saved?.second ?: false
            )
        }.reversed()
    }

    fun getStreakBadge(): Pair<String, String> {
        return when {
            streakCount >= 30 -> "🌟" to "Wellness Warrior"
            streakCount >= 14 -> "🌳" to "Deeply Rooted"
            streakCount >= 7 -> "🌱" to "Growing Strong"
            streakCount >= 3 -> "🔥" to "Getting Started"
            else -> "✨" to "New Journey"
        }
    }

    // --- Journal ---
    fun getJournalEntries(): List<Triple<String, String, String>> {
        val json = prefs.getString(KEY_JOURNAL, "[]") ?: "[]"
        val arr = JSONArray(json)
        val result = mutableListOf<Triple<String, String, String>>()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            result.add(
                Triple(
                    obj.getString("date"),
                    obj.getString("prompt"),
                    obj.getString("text")
                )
            )
        }
        return result.reversed() // newest first
    }

    fun addJournalEntry(prompt: String, text: String) {
        val json = prefs.getString(KEY_JOURNAL, "[]") ?: "[]"
        val arr = JSONArray(json)
        val entry = JSONObject().apply {
            put("date", today)
            put("prompt", prompt)
            put("text", text)
        }
        arr.put(entry)
        prefs.edit().putString(KEY_JOURNAL, arr.toString()).apply()
        recordTodayActive()
    }

    // --- Full Reset ---
    fun clearAllData() {
        prefs.edit().clear().apply()
    }

    fun resetDailyData() {
        prefs.edit()
            .putInt(KEY_NOURISHMENT, 0)
            .putInt(KEY_SLEEP, 0)
            .putInt(KEY_MOVEMENT, 0)
            .putInt(KEY_HYDRATION_CUPS, 0)
            .putInt(KEY_DAILY_CALORIES, 0)
            .apply()
    }

    fun getCommunityPosts(): List<CommunityPost> {
        val json = prefs.getString(KEY_COMMUNITY_POSTS, "[]") ?: "[]"
        val arr = JSONArray(json)
        val result = mutableListOf<CommunityPost>()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            result.add(
                CommunityPost(
                    id = obj.optString("id", ""),
                    category = obj.optString("category", "General"),
                    text = obj.optString("text", ""),
                    author = obj.optString("author", "Anonymous"),
                    likes = obj.optInt("likes", 0),
                    claps = obj.optInt("claps", 0),
                    time = obj.optString("time", "Just now")
                )
            )
        }
        return result.reversed()
    }

    fun addCommunityPost(category: String, text: String, authorName: String) {
        val json = prefs.getString(KEY_COMMUNITY_POSTS, "[]") ?: "[]"
        val arr = JSONArray(json)
        val entry = JSONObject().apply {
            put("id", java.util.UUID.randomUUID().toString())
            put("category", category)
            put("text", text)
            put("author", authorName)
            put("likes", 0)
            put("claps", 0)
            put("time", "Just now")
        }
        arr.put(entry)
        prefs.edit().putString(KEY_COMMUNITY_POSTS, arr.toString()).apply()
        recordTodayActive()
    }

    fun updateCommunityPostReactions(postId: String, likes: Int, claps: Int) {
        val json = prefs.getString(KEY_COMMUNITY_POSTS, "[]") ?: "[]"
        val arr = JSONArray(json)
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            if (obj.getString("id") == postId) {
                obj.put("likes", likes)
                obj.put("claps", claps)
                break
            }
        }
        prefs.edit().putString(KEY_COMMUNITY_POSTS, arr.toString()).apply()
    }

    // --- Daily Intention ---
    var dailyIntention: String
        get() = prefs.getString(KEY_DAILY_INTENTION, "") ?: ""
        set(value) = prefs.edit().putString(KEY_DAILY_INTENTION, value).apply()

    // --- Custom Breathing Cycles ---
    fun getCustomBreaths(): List<CustomBreath> {
        val json = prefs.getString(KEY_CUSTOM_BREATHS, "[]") ?: "[]"
        val arr = JSONArray(json)
        val result = mutableListOf<CustomBreath>()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            result.add(
                CustomBreath(
                    name = obj.getString("name"),
                    emoji = obj.getString("emoji"),
                    inhale = obj.getInt("inhale"),
                    hold1 = obj.getInt("hold1"),
                    exhale = obj.getInt("exhale"),
                    hold2 = obj.getInt("hold2")
                )
            )
        }
        return result
    }

    fun addCustomBreath(name: String, emoji: String, inhale: Int, hold1: Int, exhale: Int, hold2: Int) {
        val breaths = getCustomBreaths().toMutableList()
        breaths.add(CustomBreath(name, emoji, inhale, hold1, exhale, hold2))
        val arr = JSONArray()
        for (b in breaths) {
            val obj = JSONObject().apply {
                put("name", b.name)
                put("emoji", b.emoji)
                put("inhale", b.inhale)
                put("hold1", b.hold1)
                put("exhale", b.exhale)
                put("hold2", b.hold2)
            }
            arr.put(obj)
        }
        prefs.edit().putString(KEY_CUSTOM_BREATHS, arr.toString()).apply()
    }
    // --- Mindful Nourishment Logs ---
    fun getNourishLogs(): List<NourishLog> {
        val json = prefs.getString("nourish_logs", "[]") ?: "[]"
        val arr = JSONArray(json)
        val result = mutableListOf<NourishLog>()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            result.add(
                NourishLog(
                    date = obj.optString("date", ""),
                    mealName = obj.optString("mealName", ""),
                    hungerType = obj.optString("hungerType", ""),
                    tasteRating = obj.optDouble("tasteRating", 3.0).toFloat(),
                    textureRating = obj.optDouble("textureRating", 3.0).toFloat(),
                    smellRating = obj.optDouble("smellRating", 3.0).toFloat(),
                    satisfaction = obj.optDouble("satisfaction", 3.0).toFloat(),
                    sensationNote = obj.optString("sensationNote", "")
                )
            )
        }
        return result.reversed()
    }

    fun addNourishLog(
        mealName: String,
        hungerType: String,
        taste: Float,
        texture: Float,
        smell: Float,
        satisfaction: Float,
        note: String
    ) {
        val logs = getNourishLogs().toMutableList()
        logs.add(NourishLog(today, mealName, hungerType, taste, texture, smell, satisfaction, note))
        val arr = JSONArray()
        for (l in logs) {
            val obj = JSONObject().apply {
                put("date", l.date)
                put("mealName", l.mealName)
                put("hungerType", l.hungerType)
                put("tasteRating", l.tasteRating.toDouble())
                put("textureRating", l.textureRating.toDouble())
                put("smellRating", l.smellRating.toDouble())
                put("satisfaction", l.satisfaction.toDouble())
                put("sensationNote", l.sensationNote)
            }
            arr.put(obj)
        }
        prefs.edit().putString("nourish_logs", arr.toString()).apply()
        recordTodayActive()
    }

    // --- Gentle Movement Custom Playlists ---
    fun getMovementFlow(): List<String> {
        val json = prefs.getString("movement_flow_v1", "[\"Gentle Neck Stretch\", \"Mindful Breathing\", \"Seated Arm Circles\"]") ?: "[]"
        val arr = JSONArray(json)
        val result = mutableListOf<String>()
        for (i in 0 until arr.length()) {
            result.add(arr.getString(i))
        }
        return result
    }

    fun saveMovementFlow(flow: List<String>) {
        val arr = JSONArray(flow)
        prefs.edit().putString("movement_flow_v1", arr.toString()).apply()
    }

    // --- Mindful Self-Care Checklist/Habits ---
    fun getCustomHabits(): List<String> {
        val json = prefs.getString("custom_habits_v1", "[\"Morning Stretch 🧘\", \"Hydration Cup 💧\", \"Digital Rest 📵\", \"Compassionate Note 📝\"]") ?: "[]"
        val arr = JSONArray(json)
        val result = mutableListOf<String>()
        for (i in 0 until arr.length()) {
            result.add(arr.getString(i))
        }
        return result
    }

    fun addCustomHabit(habitName: String) {
        val list = getCustomHabits().toMutableList()
        if (!list.contains(habitName)) {
            list.add(habitName)
            val arr = JSONArray(list)
            prefs.edit().putString("custom_habits_v1", arr.toString()).apply()
        }
    }

    fun deleteCustomHabit(habitName: String) {
        val list = getCustomHabits().toMutableList()
        list.remove(habitName)
        val arr = JSONArray(list)
        prefs.edit().putString("custom_habits_v1", arr.toString()).apply()
    }

    fun getCheckedHabitsForDate(dateStr: String): Set<String> {
        return prefs.getStringSet("habits_checked_$dateStr", emptySet()) ?: emptySet()
    }

    fun setCheckedHabitsForDate(dateStr: String, checked: Set<String>) {
        prefs.edit().putStringSet("habits_checked_$dateStr", checked).apply()
        recordTodayActive()
    }
}

data class CommunityPost(
    val id: String,
    val category: String,
    val text: String,
    val author: String,
    var likes: Int,
    var claps: Int,
    val time: String
)

data class CustomBreath(
    val name: String,
    val emoji: String,
    val inhale: Int,
    val hold1: Int,
    val exhale: Int,
    val hold2: Int
)

data class NourishLog(
    val date: String,
    val mealName: String,
    val hungerType: String,
    val tasteRating: Float,
    val textureRating: Float,
    val smellRating: Float,
    val satisfaction: Float,
    val sensationNote: String
)

data class DailyCheckIn(
    val date: String,
    val dayLabel: String,
    val moodLabel: String,
    val gentleMode: Boolean
)
