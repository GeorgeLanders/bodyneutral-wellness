package com.example.bodyneutralwellness.ui.movement

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import com.example.bodyneutralwellness.R
import com.example.bodyneutralwellness.VideoPlayer
import com.example.bodyneutralwellness.data.AmbientSoundManager
import com.example.bodyneutralwellness.data.WellnessPreferences
import com.example.bodyneutralwellness.theme.WellnessBackgroundBrush
import kotlinx.coroutines.delay

enum class MovementCategory { ALL, SEATED, LOW_IMPACT, JOYFUL }

data class VideoItem(val name: String, val resId: Int, val category: MovementCategory)

val videoList = listOf(
    VideoItem("Body Scan Relaxation", R.raw.body_scan_relaxation, MovementCategory.JOYFUL),
    VideoItem("Box Breathing", R.raw.box_breathing, MovementCategory.SEATED),
    VideoItem("Gentle Morning Stroll", R.raw.gentle_morning_stroll, MovementCategory.LOW_IMPACT),
    VideoItem("Gentle Neck Stretch", R.raw.gentle_neck_stretch, MovementCategory.SEATED),
    VideoItem("Mindful Breathing", R.raw.mindfull, MovementCategory.SEATED),
    VideoItem("Relaxation Breath", R.raw.relaxation_breath, MovementCategory.SEATED),
    VideoItem("Seated Arm Circles", R.raw.seated_arm_circles, MovementCategory.SEATED),
    VideoItem("Seated Leg Lifts", R.raw.seated_leg_lifts, MovementCategory.SEATED),
    VideoItem("Seated Torso Twist", R.raw.seated_torso_twist, MovementCategory.SEATED),
    VideoItem("Seated Forward Fold", R.raw.seated_forward_fold, MovementCategory.SEATED),
    VideoItem("Cat-Cow Stretch", R.raw.cat_cowstretch, MovementCategory.LOW_IMPACT),
    VideoItem("Side Steps", R.raw.side_steps, MovementCategory.LOW_IMPACT),
    VideoItem("Joyful Push", R.raw.will_push, MovementCategory.JOYFUL)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovementScreen(
    onItemClick: (NavKey) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs = remember { WellnessPreferences(context) }
    val soundManager = remember { AmbientSoundManager(context) }
    var activeSound by remember { mutableStateOf(soundManager.getActiveSound()) }

    DisposableEffect(Unit) {
        onDispose {
            soundManager.stopSound()
        }
    }
    var selectedCategory by remember { mutableStateOf(MovementCategory.ALL) }

    // Playlist/Flow builder states
    var isBuilderMode by remember { mutableStateOf(false) }
    var currentFlow by remember { mutableStateOf(prefs.getMovementFlow()) }

    // Flow Player active playing state
    var isPlayingFlow by remember { mutableStateOf(false) }
    var activeFlowIndex by remember { mutableStateOf(0) }
    var activeTimerSeconds by remember { mutableIntStateOf(30) }
    var isFlowPaused by remember { mutableStateOf(false) }
    var showFlowComplete by remember { mutableStateOf(false) }

    val displayedVideos = if (selectedCategory == MovementCategory.ALL) {
        videoList
    } else {
        videoList.filter { it.category == selectedCategory }
    }

    // Timer effect for movement flow player
    LaunchedEffect(isPlayingFlow, activeFlowIndex, isFlowPaused) {
        if (isPlayingFlow && !isFlowPaused) {
            while (activeTimerSeconds > 0) {
                delay(1000L)
                activeTimerSeconds--
            }
            // Move to next step or finish
            if (activeFlowIndex < currentFlow.size - 1) {
                activeFlowIndex++
                activeTimerSeconds = 30
            } else {
                // Done!
                isPlayingFlow = false
                showFlowComplete = true
                prefs.movementMinutes = prefs.movementMinutes + (currentFlow.size * 30 / 60).coerceAtLeast(1)
            }
        }
    }

    Box(modifier = modifier.fillMaxSize().background(WellnessBackgroundBrush())) {
        if (isPlayingFlow && currentFlow.isNotEmpty()) {
            // Ambient Flow Player Full-screen Overlay
            val activeMovementName = currentFlow.getOrNull(activeFlowIndex) ?: "Quiet Breath"
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .safeDrawingPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { isPlayingFlow = false }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Exit Player", tint = MaterialTheme.colorScheme.primary)
                    }
                    Text(
                        "Step ${activeFlowIndex + 1} of ${currentFlow.size}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Breathing/Movement Timer
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        activeMovementName,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Listen to your body. Move gently.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(48.dp))

                    // Timer Circular Graphic
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(200.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$activeTimerSeconds",
                                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 54.sp, fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                "seconds left",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Ambient Selector
                    Text(
                        "Soothing Soundscape",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(
                            Triple("None", "🔇", "None"),
                            Triple("Gentle Rain", "🌧️", "Rain"),
                            Triple("Ocean Waves", "🌊", "Ocean"),
                            Triple("Cozy Hearth", "🔥", "Hearth")
                        ).forEach { (soundName, emoji, label) ->
                            val isSoundSelected = activeSound == soundName
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        activeSound = soundName
                                        soundManager.startSound(soundName)
                                    },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSoundSelected) MaterialTheme.colorScheme.secondaryContainer
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(emoji, fontSize = 14.sp)
                                    Text(label, style = MaterialTheme.typography.labelSmall, fontSize = 9.sp)
                                }
                            }
                        }
                    }
                }

                // Controls
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                isFlowPaused = !isFlowPaused
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(if (isFlowPaused) "Resume" else "Pause")
                        }

                        Button(
                            onClick = {
                                if (activeFlowIndex < currentFlow.size - 1) {
                                    activeFlowIndex++
                                    activeTimerSeconds = 30
                                } else {
                                    isPlayingFlow = false
                                    showFlowComplete = true
                                    prefs.movementMinutes = prefs.movementMinutes + (currentFlow.size * 30 / 60).coerceAtLeast(1)
                                }
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Skip Step")
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        } else {
            // Standard Screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Gentle Movement",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Move your body at your own speed, with love.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }

                    // Button to toggle playlist builder
                    Button(
                        onClick = { isBuilderMode = !isBuilderMode },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isBuilderMode) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Text(
                            if (isBuilderMode) "Close Flow" else "Build Flow",
                            color = if (isBuilderMode) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                AnimatedVisibility(visible = showFlowComplete) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.65f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                "Flow Complete",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Text(
                                "Notice what changed: breath, shoulders, energy, mood, or simply the fact that you showed up.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.85f)
                            )
                            TextButton(onClick = { showFlowComplete = false }) {
                                Text("Close")
                            }
                        }
                    }
                }

                // Builder Playlist Panel
                AnimatedVisibility(visible = isBuilderMode) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Your Active Gentle Flow (${currentFlow.size} steps)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            if (currentFlow.isEmpty()) {
                                Text(
                                    "Tap '+' below to add stretching/breathing steps to your personal flow playlist.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                                )
                            } else {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = {
                                            activeFlowIndex = 0
                                            activeTimerSeconds = 30
                                            isFlowPaused = false
                                            isPlayingFlow = true
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Play My Flow")
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            currentFlow = emptyList()
                                            prefs.saveMovementFlow(emptyList())
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Reset Flow")
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                // Quick step summary horizontally
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    currentFlow.forEachIndexed { idx, item ->
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.surface
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("${idx + 1}. $item", style = MaterialTheme.typography.labelSmall)
                                                Spacer(modifier = Modifier.width(2.dp))
                                                IconButton(
                                                    onClick = {
                                                        val updated = currentFlow.toMutableList()
                                                        updated.removeAt(idx)
                                                        currentFlow = updated
                                                        prefs.saveMovementFlow(updated)
                                                    },
                                                    modifier = Modifier.size(16.dp)
                                                ) {
                                                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red, modifier = Modifier.size(12.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Filter Chips
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedCategory == MovementCategory.ALL,
                        onClick = { selectedCategory = MovementCategory.ALL },
                        label = { Text("All") }
                    )
                    FilterChip(
                        selected = selectedCategory == MovementCategory.SEATED,
                        onClick = { selectedCategory = MovementCategory.SEATED },
                        label = { Text("Seated") }
                    )
                    FilterChip(
                        selected = selectedCategory == MovementCategory.LOW_IMPACT,
                        onClick = { selectedCategory = MovementCategory.LOW_IMPACT },
                        label = { Text("Low-Impact") }
                    )
                    FilterChip(
                        selected = selectedCategory == MovementCategory.JOYFUL,
                        onClick = { selectedCategory = MovementCategory.JOYFUL },
                        label = { Text("Joyful") }
                    )
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 88.dp)
                ) {
                    items(displayedVideos) { video ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    if (isBuilderMode) {
                                        val updated = currentFlow.toMutableList()
                                        if (updated.size < 5) {
                                            updated.add(video.name)
                                            currentFlow = updated
                                            prefs.saveMovementFlow(updated)
                                        }
                                    } else {
                                        onItemClick(VideoPlayer(videoName = video.name, videoResId = video.resId))
                                    }
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("▶", color = MaterialTheme.colorScheme.primary)
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(
                                            text = video.name,
                                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = video.category.name.replace("_", " "),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                if (isBuilderMode) {
                                    IconButton(
                                        onClick = {
                                            val updated = currentFlow.toMutableList()
                                            if (updated.size < 5 && !updated.contains(video.name)) {
                                                updated.add(video.name)
                                                currentFlow = updated
                                                prefs.saveMovementFlow(updated)
                                            }
                                        }
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Add step", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
