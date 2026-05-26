package com.example.bodyneutralwellness.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import com.example.bodyneutralwellness.GearGuide
import com.example.bodyneutralwellness.WellnessWins
import com.example.bodyneutralwellness.Journal
import com.example.bodyneutralwellness.Breathing
import com.example.bodyneutralwellness.Nourish
import com.example.bodyneutralwellness.Sos
import com.example.bodyneutralwellness.Community
import com.example.bodyneutralwellness.AudioDiary
import com.example.bodyneutralwellness.BreatheCustomizer
import com.example.bodyneutralwellness.Movement
import com.example.bodyneutralwellness.data.RecommendationDestination
import com.example.bodyneutralwellness.data.RecommendationEngine
import com.example.bodyneutralwellness.data.WellnessPreferences
import com.example.bodyneutralwellness.data.WellnessRecommendation
import com.example.bodyneutralwellness.data.WellnessSnapshot
import com.example.bodyneutralwellness.data.WeeklyReflection
import com.example.bodyneutralwellness.data.WeeklyReflectionEngine
import com.example.bodyneutralwellness.data.DailyCheckIn
import com.example.bodyneutralwellness.theme.WellnessBackgroundBrush
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun DashboardScreen(
    onItemClick: (NavKey) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs = remember { WellnessPreferences(context) }

    // Live custom habits query
    val todayStr = remember { LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) }
    val moods = listOf("\uD83D\uDE0A", "\uD83D\uDE10", "\uD83D\uDE14", "\uD83E\uDD17", "\uD83D\uDCAA")
    val moodLabels = listOf("Great", "Okay", "Low", "Cozy", "Strong")
    val savedCheckIn = remember(todayStr) { prefs.getDailyCheckIn(todayStr) }
    var isBadBodyImageDay by remember { mutableStateOf(savedCheckIn?.second ?: false) }
    var selectedMood by remember {
        mutableStateOf(savedCheckIn?.first?.let { moodLabels.indexOf(it) }?.takeIf { it >= 0 } ?: -1)
    }
    var recentCheckIns by remember { mutableStateOf(prefs.getRecentDailyCheckIns(7)) }
    var recommendationFeedback by remember { mutableStateOf(prefs.getRecommendationFeedback()) }
    var habitItems by remember { mutableStateOf(prefs.getCustomHabits()) }
    var checkedHabits by remember { mutableStateOf(prefs.getCheckedHabitsForDate(todayStr)) }

    val name = prefs.userName.ifBlank { "Beautiful" }
    val streak = prefs.streakCount
    val badge = prefs.getStreakBadge()

    val greeting = remember(name, selectedMood, isBadBodyImageDay, streak) {
        val dayPart = when (LocalTime.now().hour) {
            in 5..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            in 17..21 -> "Good evening"
            else -> "You're here"
        }
        val moodPart = moodLabels.getOrNull(selectedMood)?.let { " You checked in as $it." }.orEmpty()
        val gentlePart = if (isBadBodyImageDay) " Gentle mode is on, so softer choices come first." else ""
        "$dayPart, $name.$moodPart$gentlePart"
    }
    val wellnessSnapshot = WellnessSnapshot(
        gentleMode = isBadBodyImageDay,
        selectedMoodLabel = moodLabels.getOrNull(selectedMood),
        streakCount = streak,
        hydrationCups = prefs.hydrationCups,
        movementMinutes = prefs.movementMinutes,
        nourishmentCount = prefs.nourishmentCount,
        sleepHours = prefs.sleepHours,
        journalEntriesCount = prefs.getJournalEntries().size,
        checkedHabitCount = checkedHabits.size,
        totalHabitCount = habitItems.size,
        dailyIntention = prefs.dailyIntention,
        recommendationFeedback = recommendationFeedback
    )
    val recommendations = RecommendationEngine.recommendationsFor(wellnessSnapshot)
    val weeklyReflection = WeeklyReflectionEngine.reflectionFor(wellnessSnapshot)

    val defaultAffirmation = if (isBadBodyImageDay) {
        "It's okay to just exist today.\nYour worth is not determined by your body. Rest is productive too."
    } else {
        "Your body carried you through yesterday and will carry you through today. Celebrate that."
    }

    val affirmation = if (prefs.customAffirmation.isNotBlank() && !isBadBodyImageDay) {
        prefs.customAffirmation
    } else {
        defaultAffirmation
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(WellnessBackgroundBrush())
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
        // Greeting & Name
        Text(
            text = greeting,
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "How is your body feeling today?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 20.dp)
        )

        CheckInHistoryCard(checkIns = recentCheckIns)

        // Streak & Wins Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
                .clickable { onItemClick(WellnessWins) },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(badge.first, fontSize = 28.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Your Wellness Wins",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Text(
                        "$streak Day Streak · ${badge.second}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                    )
                }
                Text(
                    text = "➔",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }

        // Mood Selector
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                moods.forEachIndexed { index, emoji ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                selectedMood = index
                                prefs.saveDailyCheckIn(todayStr, moodLabels[index], isBadBodyImageDay)
                                recentCheckIns = prefs.getRecentDailyCheckIns(7)
                                prefs.recordTodayActive()
                            }
                            .background(
                                if (selectedMood == index) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surface
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(emoji, fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            moodLabels[index],
                            style = MaterialTheme.typography.bodySmall,
                            color = if (selectedMood == index) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Bad Body Image Day Toggle
        Surface(
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
            shape = RoundedCornerShape(16.dp),
            color = if (isBadBodyImageDay) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.SelfImprovement,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Gentle Day Mode",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = isBadBodyImageDay,
                    onCheckedChange = {
                        isBadBodyImageDay = it
                        prefs.saveDailyCheckIn(todayStr, moodLabels.getOrNull(selectedMood).orEmpty(), it)
                        recentCheckIns = prefs.getRecentDailyCheckIns(7)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.secondary,
                        checkedTrackColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                )
            }
        }

        // Affirmation / Intention Card
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isBadBodyImageDay) MaterialTheme.colorScheme.secondaryContainer
                                 else MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = if (isBadBodyImageDay) "Daily Reminder" else "Daily Affirmation",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isBadBodyImageDay) MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                           else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = affirmation,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 28.sp
                    ),
                    color = if (isBadBodyImageDay) MaterialTheme.colorScheme.onSecondaryContainer
                            else MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Start
                )
            }
        }

        Text(
            text = "Recommended for You",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        recommendations.forEach { recommendation ->
            RecommendationCard(
                recommendation = recommendation,
                onClick = {
                    onItemClick(destinationForRecommendation(recommendation.destination))
                },
                onHelpful = {
                    prefs.recordRecommendationFeedback(recommendation.destination, helpful = true)
                    recommendationFeedback = prefs.getRecommendationFeedback()
                },
                onNotToday = {
                    prefs.recordRecommendationFeedback(recommendation.destination, helpful = false)
                    recommendationFeedback = prefs.getRecommendationFeedback()
                }
            )
        }

        Button(
            onClick = {
                recommendations.firstOrNull()?.let { recommendation ->
                    onItemClick(destinationForRecommendation(recommendation.destination))
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("What should I do now?", fontWeight = FontWeight.Bold)
        }

        WeeklyReflectionCard(reflection = weeklyReflection)

        // Daily Intention Card
        val intentionThemes = listOf(
            "🌿" to "Honoring Rest",
            "🌸" to "Savoring Joy",
            "👂" to "Listening to My Body",
            "💛" to "Self-Compassion",
            "✨" to "Embracing Enough",
            "🌊" to "Flowing with Change"
        )
        var savedIntention by remember { mutableStateOf(prefs.dailyIntention) }

        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Today's Intention",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                if (savedIntention.isNotBlank()) {
                    val matchingTheme = intentionThemes.find { it.second == savedIntention }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(matchingTheme?.first ?: "🌟", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            savedIntention,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                    TextButton(
                        onClick = {
                            prefs.dailyIntention = ""
                            savedIntention = ""
                        }
                    ) {
                        Text("Change Intention", style = MaterialTheme.typography.bodySmall)
                    }
                } else {
                    Text(
                        "Choose a theme to carry with you today:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        intentionThemes.take(3).forEach { (emoji, label) ->
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        prefs.dailyIntention = label
                                        savedIntention = label
                                        prefs.recordTodayActive()
                                    },
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(emoji, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        label,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        intentionThemes.drop(3).forEach { (emoji, label) ->
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        prefs.dailyIntention = label
                                        savedIntention = label
                                        prefs.recordTodayActive()
                                    },
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(emoji, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        label,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Today's Gentle Habits
        Text(
            text = "Today's Gentle Habits",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (habitItems.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Text(
                    text = "No self-care items defined for today. Head to the Tracker tab to add simple acts of kindness!",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            val allDone = habitItems.all { checkedHabits.contains(it) }

            habitItems.forEach { habit ->
                val isChecked = checkedHabits.contains(habit)
                HabitRow(
                    title = habit,
                    subtitle = if (isChecked) "Completed with kindness ✓" else "Tap to check off",
                    checked = isChecked,
                    onCheckedChange = {
                        val updated = checkedHabits.toMutableSet()
                        if (isChecked) updated.remove(habit) else updated.add(habit)
                        checkedHabits = updated
                        prefs.setCheckedHabitsForDate(todayStr, updated)
                    }
                )
            }

            if (allDone) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🌸", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "Look at how beautifully you cared for yourself today!",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                "Every small act of kindness builds your inner home.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Gear Guide Button
        OutlinedButton(
            onClick = { onItemClick(GearGuide) },
            modifier = Modifier.fillMaxWidth().height(56.dp).padding(bottom = 12.dp),
            shape = RoundedCornerShape(16.dp),
            border = ButtonDefaults.outlinedButtonBorder(enabled = true)
        ) {
            Text(
                text = "Comfort & Gear Guide",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Community Circles
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
                .clickable { onItemClick(Community) },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👥", fontSize = 28.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Community Circles",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        "Connect in a safe, shame-free space.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                }
                Text(
                    text = "➔",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        // Quick Actions Title
        Text(
            text = "Explore Practices",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Quick Action Cards (Journal, Breathing, Nourish)
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCard(
                title = "Journal",
                emoji = "📖",
                modifier = Modifier.weight(1f),
                onClick = { onItemClick(Journal) }
            )
            QuickActionCard(
                title = "Breathe",
                emoji = "🌊",
                modifier = Modifier.weight(1f),
                onClick = { onItemClick(Breathing) }
            )
            QuickActionCard(
                title = "Nourish",
                emoji = "🥗",
                modifier = Modifier.weight(1f),
                onClick = { onItemClick(Nourish) }
            )
        }

        // Second row of quick actions
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCard(
                title = "Voice Diary",
                emoji = "🎤",
                modifier = Modifier.weight(1f),
                onClick = { onItemClick(AudioDiary) }
            )
            QuickActionCard(
                title = "Custom Rhythm",
                emoji = "💫",
                modifier = Modifier.weight(1f),
                onClick = { onItemClick(BreatheCustomizer) }
            )
        }

        Spacer(modifier = Modifier.height(80.dp)) // space for bottom nav
        }

        // Floating SOS Button in bottom-right corner
        FloatingActionButton(
            onClick = { onItemClick(Sos) },
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 96.dp, end = 20.dp) // padded above the bottom nav bar
        ) {
            Text("SOS", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp))
        }
    }
}

private fun destinationForRecommendation(destination: RecommendationDestination): NavKey {
    return when (destination) {
        RecommendationDestination.Breathing -> Breathing
        RecommendationDestination.Journal -> Journal
        RecommendationDestination.Movement -> Movement
        RecommendationDestination.Nourish -> Nourish
        RecommendationDestination.Sos -> Sos
        RecommendationDestination.WellnessWins -> WellnessWins
    }
}

@Composable
fun CheckInHistoryCard(checkIns: List<DailyCheckIn>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Daily Check-In Pattern",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                checkIns.forEach { checkIn ->
                    val label = if (checkIn.moodLabel.isBlank()) "-" else checkIn.moodLabel.take(1)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            checkIn.dayLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = CircleShape,
                            color = if (checkIn.gentleMode) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    label,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecommendationCard(
    recommendation: WellnessRecommendation,
    onClick: () -> Unit,
    onHelpful: () -> Unit,
    onNotToday: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "AI",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    recommendation.title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    recommendation.reason,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (recommendation.feedback.helpfulCount > 0 || recommendation.feedback.dismissedCount > 0) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Learned: ${recommendation.feedback.helpfulCount} helpful, ${recommendation.feedback.dismissedCount} not today",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                TextButton(onClick = onClick) {
                    Text(recommendation.actionLabel)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(onClick = onHelpful, contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)) {
                        Text("Helpful", style = MaterialTheme.typography.labelSmall)
                    }
                    TextButton(onClick = onNotToday, contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)) {
                        Text("Not today", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

@Composable
fun WeeklyReflectionCard(reflection: WeeklyReflection) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.45f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                reflection.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                reflection.body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.86f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                reflection.focus,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    emoji: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(emoji, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun HabitRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clickable { onCheckedChange(!checked) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (checked) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                             else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (checked) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.primaryContainer
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (checked) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Checked",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        Icons.Outlined.CheckCircle,
                        contentDescription = "Unchecked",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
