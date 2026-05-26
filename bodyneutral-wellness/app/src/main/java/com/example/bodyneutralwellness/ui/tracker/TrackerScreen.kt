package com.example.bodyneutralwellness.ui.tracker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bodyneutralwellness.data.WellnessPreferences
import com.example.bodyneutralwellness.theme.WellnessBackgroundBrush
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackerScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val prefs = remember { WellnessPreferences(context) }

    var nourishmentCount by remember { mutableStateOf(prefs.nourishmentCount) }
    var sleepHours by remember { mutableStateOf(prefs.sleepHours) }
    var movementMinutes by remember { mutableStateOf(prefs.movementMinutes) }
    var dailyCalories by remember { mutableStateOf(prefs.dailyCalories) }
    val showCalories = prefs.showCaloriesSetting

    // Custom habits lists
    var habitItems by remember { mutableStateOf(prefs.getCustomHabits()) }
    val todayStr = remember { LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) }
    var checkedHabits by remember { mutableStateOf(prefs.getCheckedHabitsForDate(todayStr)) }
    var newHabitName by remember { mutableStateOf("") }
    var showAddHabitDialog by remember { mutableStateOf(false) }

    // Visual weekly list (past 7 days including today)
    val past7Days = remember {
        (0..6).map { offset ->
            val date = LocalDate.now().minusDays(offset.toLong())
            val dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
            val dayName = date.dayOfWeek.name.take(3)
            Triple(dateStr, dayName, date.dayOfMonth)
        }.reversed()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WellnessBackgroundBrush())
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Wellness & Self-Care",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // Guilt-Free 7-Day Completion Grid
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Self-Care over the Past Week 🌸",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    past7Days.forEach { (dateStr, dayName, dayNum) ->
                        val doneCount = prefs.getCheckedHabitsForDate(dateStr).size
                        val isToday = dateStr == todayStr

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(dayName, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isToday) MaterialTheme.colorScheme.primary else if (doneCount > 0) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface,
                                modifier = Modifier.size(36.dp),
                                shadowElevation = 1.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        "$dayNum",
                                        fontWeight = FontWeight.Bold,
                                        color = if (isToday) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }
                            if (doneCount > 0) {
                                Text("✓".repeat(doneCount.coerceAtMost(3)), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            } else {
                                Text("•", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                            }
                        }
                    }
                }
            }
        }

        // Checklist Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Kind Self-Care Habits",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "Little acts of compassion for yourself.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { showAddHabitDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add custom habit", tint = MaterialTheme.colorScheme.primary)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                habitItems.forEach { habit ->
                    val isChecked = checkedHabits.contains(habit)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val updated = checkedHabits.toMutableSet()
                                if (isChecked) updated.remove(habit) else updated.add(habit)
                                checkedHabits = updated
                                prefs.setCheckedHabitsForDate(todayStr, updated)
                            }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = {
                                    val updated = checkedHabits.toMutableSet()
                                    if (isChecked) updated.remove(habit) else updated.add(habit)
                                    checkedHabits = updated
                                    prefs.setCheckedHabitsForDate(todayStr, updated)
                                }
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                habit,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (isChecked) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        IconButton(
                            onClick = {
                                prefs.deleteCustomHabit(habit)
                                habitItems = prefs.getCustomHabits()
                                val updated = checkedHabits.toMutableSet()
                                updated.remove(habit)
                                checkedHabits = updated
                                prefs.setCheckedHabitsForDate(todayStr, updated)
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete custom habit",
                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.5f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                }
            }
        }

        // Nourishment
        TrackerCard(
            title = "Nourishment",
            subtitle = "Nutrient-dense items added to meals",
            value = nourishmentCount.toString(),
            unit = "items",
            onDecrement = {
                if (nourishmentCount > 0) {
                    nourishmentCount--
                    prefs.nourishmentCount = nourishmentCount
                    prefs.recordTodayActive()
                }
            },
            onIncrement = {
                nourishmentCount++
                prefs.nourishmentCount = nourishmentCount
                prefs.recordTodayActive()
            }
        )

        // Sleep
        TrackerCard(
            title = "Restorative Sleep",
            subtitle = "Listen to your body's need for rest",
            value = sleepHours.toString(),
            unit = "hours",
            onDecrement = {
                if (sleepHours > 0) {
                    sleepHours--
                    prefs.sleepHours = sleepHours
                    prefs.recordTodayActive()
                }
            },
            onIncrement = {
                sleepHours++
                prefs.sleepHours = sleepHours
                prefs.recordTodayActive()
            }
        )

        // Movement
        TrackerCard(
            title = "Joyful Movement",
            subtitle = "Any physical activity that feels good",
            value = movementMinutes.toString(),
            unit = "minutes",
            onDecrement = {
                if (movementMinutes >= 5) {
                    movementMinutes -= 5
                    prefs.movementMinutes = movementMinutes
                    prefs.recordTodayActive()
                }
            },
            onIncrement = {
                movementMinutes += 5
                prefs.movementMinutes = movementMinutes
                prefs.recordTodayActive()
            }
        )

        // Energy Fueling (Calories) - Positive reinforcement calorie tracker
        if (showCalories) {
            TrackerCard(
                title = "Energy Awareness",
                subtitle = "Optional tracking for fuel, weight management, or performance goals",
                value = dailyCalories.toString(),
                unit = "kcal",
                onDecrement = {
                    if (dailyCalories >= 100) {
                        dailyCalories -= 100
                        prefs.dailyCalories = dailyCalories
                        prefs.recordTodayActive()
                    }
                },
                onIncrement = {
                    dailyCalories += 100
                    prefs.dailyCalories = dailyCalories
                    prefs.recordTodayActive()
                }
            )

            Card(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = {
                        dailyCalories += 250
                        prefs.dailyCalories = dailyCalories
                        prefs.recordTodayActive()
                    }) {
                        Text("+250 kcal Fuel")
                    }
                    TextButton(onClick = {
                        dailyCalories += 500
                        prefs.dailyCalories = dailyCalories
                        prefs.recordTodayActive()
                    }) {
                        Text("+500 kcal Fuel")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(88.dp)) // bottom nav padding
    }

    // Add Custom Habit Dialog
    if (showAddHabitDialog) {
        AlertDialog(
            onDismissRequest = { showAddHabitDialog = false },
            title = { Text("New Self-Care Habit") },
            text = {
                OutlinedTextField(
                    value = newHabitName,
                    onValueChange = { newHabitName = it },
                    label = { Text("What act of kindness?") },
                    placeholder = { Text("e.g. 15-minute nap 💤") },
                    shape = RoundedCornerShape(12.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newHabitName.isNotBlank()) {
                            prefs.addCustomHabit(newHabitName)
                            habitItems = prefs.getCustomHabits()
                            newHabitName = ""
                            showAddHabitDialog = false
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddHabitDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun TrackerCard(
    title: String,
    subtitle: String,
    value: String,
    unit: String,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    value,
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold, fontSize = 48.sp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    unit,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(
                    onClick = onDecrement,
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("-", style = MaterialTheme.typography.titleLarge)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = onIncrement,
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        }
    }
}
