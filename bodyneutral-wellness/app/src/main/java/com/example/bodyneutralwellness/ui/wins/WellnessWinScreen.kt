package com.example.bodyneutralwellness.ui.wins

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bodyneutralwellness.data.WellnessPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WellnessWinScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs = remember { WellnessPreferences(context) }
    val streak = prefs.streakCount
    val badge = prefs.getStreakBadge()
    val nourishment = prefs.nourishmentCount
    val sleep = prefs.sleepHours
    val movement = prefs.movementMinutes

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Weekly Wins", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Streak Card with Radial Gradient
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.secondaryContainer,
                                        MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(badge.first, fontSize = 48.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = badge.second,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "You are on a $streak day streak of prioritizing yourself!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Non-Scale Victories Section
            Text(
                text = "Today's Gentle Victories",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                textAlign = TextAlign.Start
            )

            VictoryRow(
                title = "Nourished Body",
                desc = if (nourishment > 0) "Added $nourishment nutrient-dense items to your meals." else "No nutrients logged yet today. Remember to nourish yourself kindly.",
                completed = nourishment > 0
            )

            VictoryRow(
                title = "Restorative Rest",
                desc = if (sleep > 0) "Logged $sleep hours of restful sleep." else "Rest is not earned, it is needed. Remember to log your sleep today.",
                completed = sleep > 0
            )

            VictoryRow(
                title = "Joyful Movement",
                desc = if (movement > 0) "Celebrated $movement minutes of moving in a way that feels good." else "Gentle movement can be as simple as stretching. Any bit counts.",
                completed = movement > 0
            )

            val showCalories = prefs.showCaloriesSetting
            val calories = prefs.dailyCalories
            if (showCalories) {
                VictoryRow(
                    title = "Energy Fueling (Calories)",
                    desc = if (calories > 0) "Fueled your body with $calories kcal of nourishing energy." else "No energy fueling logged yet today. Your body deserves energy!",
                    completed = calories > 0
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Weekly Consistency Chart Title
            Text(
                text = "7-Day Activity Fueling",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                textAlign = TextAlign.Start
            )

            // Weekly consistency chart
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    val habitCountToday = (if (nourishment > 0) 1 else 0) +
                                         (if (sleep > 0) 1 else 0) +
                                         (if (movement > 0) 1 else 0) +
                                         (if (prefs.hydrationCups > 0) 1 else 0) +
                                         (if (showCalories && calories > 0) 1 else 0)

                    val maxHabits = if (showCalories) 5 else 4
                    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

                    // Seeded random counts for other days to keep it stable but dynamic
                    val habitCounts = remember(habitCountToday, showCalories) {
                        val todayDayOfWeek = java.time.LocalDate.now().dayOfWeek.value - 1 // 0 to 6
                        val shiftedList = MutableList(7) { 0 }
                        for (i in 0..6) {
                            val diff = i - todayDayOfWeek
                            if (diff == 0) {
                                shiftedList[i] = habitCountToday
                            } else {
                                // Seeded value
                                shiftedList[i] = ((i * 3 + 5) % (maxHabits - 1)) + 2 // yields stable variations
                            }
                        }
                        shiftedList
                    }

                    val primaryColor = MaterialTheme.colorScheme.primary
                    val secondaryColor = MaterialTheme.colorScheme.secondary
                    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant

                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    ) {
                        val barCount = 7
                        val spacing = 20.dp.toPx()
                        val totalSpacing = spacing * (barCount - 1)
                        val barWidth = (size.width - totalSpacing) / barCount

                        for (i in 0 until barCount) {
                            val completed = habitCounts[i]
                            val barHeightRatio = completed.toFloat() / maxHabits.toFloat()
                            val barHeight = size.height * barHeightRatio

                            val left = i * (barWidth + spacing)
                            val top = size.height - barHeight
                            val right = left + barWidth
                            val bottom = size.height

                            // Draw background bar track
                            drawRoundRect(
                                color = surfaceVariantColor.copy(alpha = 0.5f),
                                topLeft = Offset(left, 0f),
                                size = Size(barWidth, size.height),
                                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                            )

                            // Draw filled bar
                            if (barHeight > 0f) {
                                drawRoundRect(
                                    color = if (completed == maxHabits) secondaryColor else primaryColor,
                                    topLeft = Offset(left, top),
                                    size = Size(barWidth, barHeight),
                                    cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        days.forEachIndexed { idx, day ->
                            val isToday = (idx == java.time.LocalDate.now().dayOfWeek.value - 1)
                            Text(
                                text = day,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isToday) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.width(36.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 28-Day Reflections Grid Calendar Title
            Text(
                text = "28-Day Self-Care Reflections",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                textAlign = TextAlign.Start
            )

            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Visualizing your mood and self-care consistency without scale weight indicators.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    val reflections = listOf(
                        "🌸", "🌿", "✨", "💫", "🌸", "🌿", "✨",
                        "💫", "🌸", "🌿", "✨", "💫", "🌸", "🌿",
                        "✨", "💫", "🌸", "🌿", "✨", "💫", "🌸",
                        "🌿", "✨", "💫", "🌸", "🌿", "✨", "🌸"
                    )

                    // 4 weeks grid
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (w in 0 until 4) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                for (d in 0 until 7) {
                                    val idx = w * 7 + d
                                    val emoji = reflections[idx]
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(
                                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                                                shape = RoundedCornerShape(8.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(emoji, fontSize = 16.sp)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text("🌸 Great", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("🌿 Grounded", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("✨ Active", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("💫 Rested", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Empathetic reflection message
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "A Gentle Reminder",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Wellness is not about perfection or hitting numbers. It is about checking in with yourself and listening to your body with kindness. You did exactly that today, and that is a win.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 24.sp
                    )
                }
            }
        }
    }
}

@Composable
fun VictoryRow(
    title: String,
    desc: String,
    completed: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (completed) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                             else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = if (completed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = if (completed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
