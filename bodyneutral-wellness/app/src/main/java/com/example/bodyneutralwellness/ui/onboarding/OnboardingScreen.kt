package com.example.bodyneutralwellness.ui.onboarding

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.bodyneutralwellness.data.WellnessPreferences
import com.example.bodyneutralwellness.theme.WellnessBackgroundBrush

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs = remember { WellnessPreferences(context) }

    var currentStep by remember { mutableStateOf(0) }
    var selectedGoals by remember { mutableStateOf(setOf<String>()) }
    var selectedMobility by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }

    val goals = listOf(
        "Supportive weight management without shame",
        "Building strength, stamina, and confidence",
        "More energy to get through my day",
        "Better quality sleep and deeper rest",
        "Moving my body in a way that feels good",
        "A more mindful relationship with food",
        "Managing stress with practical tools",
        "Finding a community that understands me"
    )

    val mobilityOptions = listOf(
        "seated" to "I prefer seated or low-impact movements",
        "supported" to "I can stand/walk but need joint support",
        "mix" to "I'm looking for a mix of everything"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WellnessBackgroundBrush())
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Progress Dots
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .size(if (index == currentStep) 12.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (index <= currentStep) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outlineVariant
                        )
                )
            }
        }

        // Content Area
        AnimatedContent(
            targetState = currentStep,
            transitionSpec = {
                slideInHorizontally { it } + fadeIn() togetherWith
                slideOutHorizontally { -it } + fadeOut()
            },
            label = "stepTransition",
            modifier = Modifier.weight(1f)
        ) { step ->
            when (step) {
                0 -> GoalsStep(goals, selectedGoals) { selectedGoals = it }
                1 -> MobilityStep(mobilityOptions, selectedMobility) { selectedMobility = it }
                2 -> NameStep(userName) { userName = it }
            }
        }

        // Navigation Buttons
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (currentStep > 0) {
                OutlinedButton(
                    onClick = { currentStep-- },
                    modifier = Modifier.height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Back", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            Button(
                onClick = {
                    if (currentStep < 2) {
                        currentStep++
                    } else {
                        // Save and finish
                        prefs.userName = userName.ifBlank { "Friend" }
                        prefs.wellnessGoals = selectedGoals
                        prefs.mobilityPreference = selectedMobility.ifBlank { "mix" }
                        prefs.onboardingDone = true
                        prefs.recordTodayActive()
                        onComplete()
                    }
                },
                modifier = Modifier.height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = when (currentStep) {
                    0 -> selectedGoals.isNotEmpty()
                    1 -> selectedMobility.isNotEmpty()
                    else -> true
                }
            ) {
                Text(
                    if (currentStep < 2) "Next" else "I'm Ready to Thrive",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
fun GoalsStep(
    goals: List<String>,
    selected: Set<String>,
    onSelectionChange: (Set<String>) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "What does wellness\nlook like for you?",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Select all that resonate with you",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        goals.forEach { goal ->
            val isSelected = goal in selected
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        onSelectionChange(
                            if (isSelected) selected - goal else selected + goal
                        )
                    },
                shape = RoundedCornerShape(16.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surface,
                border = if (isSelected) null
                         else ButtonDefaults.outlinedButtonBorder(enabled = true)
            ) {
                Text(
                    text = goal,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(20.dp)
                )
            }
        }
    }
}

@Composable
fun MobilityStep(
    options: List<Pair<String, String>>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Let's tailor\nyour movement",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "How would you like to move today?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        options.forEach { (key, label) ->
            val isSelected = selected == key
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onSelect(key) },
                shape = RoundedCornerShape(16.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surface,
                border = if (isSelected) null
                         else ButtonDefaults.outlinedButtonBorder(enabled = true)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(20.dp)
                )
            }
        }
    }
}

@Composable
fun NameStep(
    name: String,
    onNameChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "What should\nwe call you?",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Or leave blank — we'll call you Friend",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 18.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.55f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Text(
                text = "Your answers personalize recommendations, movement flow ideas, reminders, and coach support. You can change them later.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                lineHeight = 20.sp,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center
            )
        }

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            placeholder = { Text("Your name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            textStyle = MaterialTheme.typography.titleMedium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Our Promise to You",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No shame. No \"quick fixes.\" No judgment.\nJust practical support for all bodies, whether your goals are energy, confidence, strength, mobility, or weight management.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f),
                    lineHeight = 24.sp
                )
            }
        }
    }
}
