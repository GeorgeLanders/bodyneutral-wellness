package com.example.bodyneutralwellness.ui.breathing

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.ui.platform.LocalContext
import com.example.bodyneutralwellness.data.AmbientSoundManager
import com.example.bodyneutralwellness.data.WellnessPreferences

data class BreathPattern(
    val name: String,
    val emoji: String,
    val inhale: Int,  // seconds
    val hold1: Int,   // hold after inhale (0 = skip)
    val exhale: Int,
    val hold2: Int    // hold after exhale (0 = skip)
)

val patterns = listOf(
    BreathPattern("Box Breathing", "🌊", 4, 4, 4, 4),
    BreathPattern("Calming Breath", "🌿", 4, 7, 8, 0),
    BreathPattern("Simple Relax", "💫", 4, 0, 6, 0)
)

enum class BreathPhase { IDLE, INHALE, HOLD_IN, EXHALE, HOLD_OUT, DONE }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreathingScreen(
    onBack: () -> Unit,
    onNavigateToCustomizer: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs = remember { WellnessPreferences(context) }
    val soundManager = remember { AmbientSoundManager(context) }
    var activeSound by remember { mutableStateOf(soundManager.getActiveSound()) }

    // Merge default patterns with user-saved custom ones
    val customBreaths = remember { prefs.getCustomBreaths() }
    val customPatterns = customBreaths.map { cb ->
        BreathPattern(cb.name, cb.emoji, cb.inhale, cb.hold1, cb.exhale, cb.hold2)
    }
    val allPatterns = patterns + customPatterns

    DisposableEffect(Unit) {
        onDispose {
            soundManager.stopSound()
        }
    }

    var selectedPattern by remember { mutableStateOf(allPatterns[0]) }
    var phase by remember { mutableStateOf(BreathPhase.IDLE) }
    var countdown by remember { mutableIntStateOf(0) }
    var cyclesCompleted by remember { mutableIntStateOf(0) }
    val totalCycles = 5
    var isRunning by remember { mutableStateOf(false) }

    // Breathing animation scale
    val targetScale = when (phase) {
        BreathPhase.INHALE -> 1.4f
        BreathPhase.HOLD_IN -> 1.4f
        BreathPhase.EXHALE -> 0.8f
        BreathPhase.HOLD_OUT -> 0.8f
        else -> 1.0f
    }

    val animatedScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(
            durationMillis = when (phase) {
                BreathPhase.INHALE -> selectedPattern.inhale * 1000
                BreathPhase.EXHALE -> selectedPattern.exhale * 1000
                else -> 300
            },
            easing = EaseInOutSine
        ),
        label = "breathScale"
    )

    val circleColor by animateColorAsState(
        targetValue = when (phase) {
            BreathPhase.INHALE -> MaterialTheme.colorScheme.primaryContainer
            BreathPhase.HOLD_IN -> MaterialTheme.colorScheme.secondaryContainer
            BreathPhase.EXHALE -> MaterialTheme.colorScheme.tertiaryContainer
            BreathPhase.HOLD_OUT -> MaterialTheme.colorScheme.surfaceVariant
            else -> MaterialTheme.colorScheme.primaryContainer
        },
        animationSpec = tween(800),
        label = "circleColor"
    )

    val phaseLabel = when (phase) {
        BreathPhase.IDLE -> "Tap Start"
        BreathPhase.INHALE -> "Breathe In…"
        BreathPhase.HOLD_IN -> "Hold…"
        BreathPhase.EXHALE -> "Breathe Out…"
        BreathPhase.HOLD_OUT -> "Hold…"
        BreathPhase.DONE -> "Complete ✨"
    }

    // Breathing engine
    LaunchedEffect(isRunning, selectedPattern) {
        if (!isRunning) return@LaunchedEffect
        cyclesCompleted = 0
        phase = BreathPhase.IDLE

        for (cycle in 1..totalCycles) {
            // Inhale
            phase = BreathPhase.INHALE
            for (t in selectedPattern.inhale downTo 1) {
                countdown = t
                delay(1000L)
            }

            // Hold after inhale
            if (selectedPattern.hold1 > 0) {
                phase = BreathPhase.HOLD_IN
                for (t in selectedPattern.hold1 downTo 1) {
                    countdown = t
                    delay(1000L)
                }
            }

            // Exhale
            phase = BreathPhase.EXHALE
            for (t in selectedPattern.exhale downTo 1) {
                countdown = t
                delay(1000L)
            }

            // Hold after exhale
            if (selectedPattern.hold2 > 0) {
                phase = BreathPhase.HOLD_OUT
                for (t in selectedPattern.hold2 downTo 1) {
                    countdown = t
                    delay(1000L)
                }
            }

            cyclesCompleted = cycle
        }

        phase = BreathPhase.DONE
        isRunning = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Guided Breathing",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
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
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Pattern Selector (scrollable to accommodate user customs)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(allPatterns) { pattern ->
                    val isSelected = selectedPattern == pattern
                    Surface(
                        modifier = Modifier
                            .width(100.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(enabled = !isRunning) {
                                selectedPattern = pattern
                            },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surface,
                        tonalElevation = if (isSelected) 0.dp else 2.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(pattern.emoji, fontSize = 24.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                pattern.name,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                                        else MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                maxLines = 2
                            )
                        }
                    }
                }

                // "Create New" card at the end
                item {
                    Surface(
                        modifier = Modifier
                            .width(100.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(enabled = !isRunning) { onNavigateToCustomizer() },
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        tonalElevation = 1.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Create custom",
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Create New",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Soundscape Selector
            Text(
                text = "Ambient Soundscape",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
            )

            val sounds = listOf(
                Triple("None", "🔇", "None"),
                Triple("Gentle Rain", "🌧️", "Rain"),
                Triple("Ocean Waves", "🌊", "Ocean"),
                Triple("Cozy Hearth", "🔥", "Hearth")
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                sounds.forEach { (soundName, emoji, label) ->
                    val isSoundSelected = activeSound == soundName
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                activeSound = soundName
                                soundManager.startSound(soundName)
                            },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSoundSelected) MaterialTheme.colorScheme.secondaryContainer
                                else MaterialTheme.colorScheme.surface,
                        tonalElevation = if (isSoundSelected) 0.dp else 1.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(emoji, fontSize = 18.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                label,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (isSoundSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                ),
                                color = if (isSoundSelected) MaterialTheme.colorScheme.onSecondaryContainer
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.3f))

            // Animated Breathing Circle
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .scale(animatedScale)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                circleColor,
                                circleColor.copy(alpha = 0.4f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (phase != BreathPhase.IDLE && phase != BreathPhase.DONE) {
                        Text(
                            text = countdown.toString(),
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 48.sp
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    } else {
                        Text(
                            text = selectedPattern.emoji,
                            fontSize = 56.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Phase Label
            Text(
                text = phaseLabel,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Cycle Counter
            Text(
                text = if (isRunning || phase == BreathPhase.DONE) "Cycle $cyclesCompleted of $totalCycles"
                       else "${selectedPattern.inhale}-${if (selectedPattern.hold1 > 0) "${selectedPattern.hold1}-" else ""}${selectedPattern.exhale}${if (selectedPattern.hold2 > 0) "-${selectedPattern.hold2}" else ""} pattern",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.weight(0.3f))

            // Done Card or Start Button
            if (phase == BreathPhase.DONE) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🌟", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Beautifully done.",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "You just gave your nervous system a gift. Carry this calm with you.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Button(
                    onClick = {
                        phase = BreathPhase.IDLE
                        cyclesCompleted = 0
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Start Another Session", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                }
            } else if (!isRunning) {
                Button(
                    onClick = { isRunning = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "Begin Breathing",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            } else {
                OutlinedButton(
                    onClick = {
                        isRunning = false
                        phase = BreathPhase.IDLE
                        cyclesCompleted = 0
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "Stop",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
