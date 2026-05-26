package com.example.bodyneutralwellness.ui.sos

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

enum class GroundingTrigger { GENERAL, BODY_IMAGE, OVERWHELM, NOURISH_PANIC, SLEEPLESSNESS }

data class SosComfortQuote(
    val text: String,
    val trigger: GroundingTrigger
)

private val comfortQuotesPool = listOf(
    SosComfortQuote("It is okay to just exist right now. You don't have to fix anything this second.", GroundingTrigger.GENERAL),
    SosComfortQuote("Breathe. You have survived every difficult moment before this. You are safe here.", GroundingTrigger.GENERAL),
    SosComfortQuote("Your body is a home, not a billboard. It carries you through life. Let's offer it rest.", GroundingTrigger.BODY_IMAGE),
    SosComfortQuote("Your worth is completely independent of your shape, weight, or how you look today.", GroundingTrigger.BODY_IMAGE),
    SosComfortQuote("You don't have to earn the right to occupy space in this world. You are enough as you are.", GroundingTrigger.BODY_IMAGE),
    SosComfortQuote("One small task at a time. The world can wait. Right now, just focus on this single breath.", GroundingTrigger.OVERWHELM),
    SosComfortQuote("Feelings are like waves — they peak, they roll, and they pass. You can ride this wave safely.", GroundingTrigger.OVERWHELM),
    SosComfortQuote("Nourishment is an act of kindness. You deserve to eat. Savoring food adds comfort and life.", GroundingTrigger.NOURISH_PANIC),
    SosComfortQuote("There is no guilt or shame in giving your body the fuel and energy it needs to thrive.", GroundingTrigger.NOURISH_PANIC),
    SosComfortQuote("The day is done. You did enough. It is fully safe to close your eyes and let go of today.", GroundingTrigger.SLEEPLESSNESS),
    SosComfortQuote("Your mind is tired. Let the thoughts float by like clouds. Rest is your body's birthright.", GroundingTrigger.SLEEPLESSNESS)
)

private val sensorySteps = listOf(
    Triple("5. See", "Name 5 things you can see around you right now.", Icons.Default.Visibility),
    Triple("4. Touch", "Feel 4 things you can touch (e.g. your clothes, your chair, the ground).", Icons.Default.Favorite),
    Triple("3. Hear", "Listen for 3 distinct sounds in your environment.", Icons.Default.Hearing),
    Triple("2. Smell", "Identify 2 things you can smell (or scents you enjoy).", Icons.Default.Psychology),
    Triple("1. Taste", "Name 1 flavor you can taste or appreciate.", Icons.Default.Restaurant)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SosScreen(
    onBack: () -> Unit,
    onNavigateToBreathe: () -> Unit,
    onNavigateToCoach: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTrigger by remember { mutableStateOf(GroundingTrigger.GENERAL) }

    // Filtered comforting quotes
    val filteredQuotes = remember(selectedTrigger) {
        comfortQuotesPool.filter { it.trigger == selectedTrigger || it.trigger == GroundingTrigger.GENERAL }
    }

    var activeQuoteIdx by remember(filteredQuotes) { mutableIntStateOf(0) }
    var currentSensoryStep by remember { mutableIntStateOf(0) }

    // Mini Breathe Anchor States
    var miniBreatheProgress by remember { mutableIntStateOf(4) }
    var miniBreatheText by remember { mutableStateOf("Inhale") }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            if (miniBreatheProgress > 1) {
                miniBreatheProgress--
            } else {
                if (miniBreatheText == "Inhale") {
                    miniBreatheText = "Exhale"
                    miniBreatheProgress = 4
                } else {
                    miniBreatheText = "Inhale"
                    miniBreatheProgress = 4
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "SOS Grounding Comfort",
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
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                        )
                    )
                )
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // SOS Trigger warning disclaimer
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Take a slow breath. This is a judgment-free space to help you ground yourself.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            // Trigger selectors
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "What is causing you distress? 🌸",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "General" to GroundingTrigger.GENERAL,
                        "Body Image" to GroundingTrigger.BODY_IMAGE,
                        "Overwhelm" to GroundingTrigger.OVERWHELM
                    ).forEach { (label, trigger) ->
                        FilterChip(
                            selected = selectedTrigger == trigger,
                            onClick = { selectedTrigger = trigger },
                            label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "Food/Eating" to GroundingTrigger.NOURISH_PANIC,
                        "Sleepless" to GroundingTrigger.SLEEPLESSNESS
                    ).forEach { (label, trigger) ->
                        FilterChip(
                            selected = selectedTrigger == trigger,
                            onClick = { selectedTrigger = trigger },
                            label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }

            // Calming Quote Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "🌸 Grounding Card",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Text(
                        text = filteredQuotes.getOrNull(activeQuoteIdx)?.text ?: "You are safe.",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 28.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.heightIn(min = 80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (filteredQuotes.size > 1) {
                                var newIdx = activeQuoteIdx
                                while (newIdx == activeQuoteIdx) {
                                    newIdx = filteredQuotes.indices.random()
                                }
                                activeQuoteIdx = newIdx
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Show Another Grounding Card")
                    }
                }
            }

            // Guided Breath Anchor Mini card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Guided Breath Anchor",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Slow down your sympathetic response.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Mini circle breathing indicator
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                miniBreatheText,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                "$miniBreatheProgress",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            // 5-4-3-2-1 Sensory Grounding Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "5-4-3-2-1 Grounding Practice",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        "Refocus your mind on the physical world to ease panic or anxiety.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Step Indicator
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 20.dp)
                    ) {
                        sensorySteps.forEachIndexed { index, _ ->
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (index == currentSensoryStep) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                                    )
                            )
                        }
                    }

                    // Active Step Detail
                    val step = sensorySteps[currentSensoryStep]
                    AnimatedContent(
                        targetState = step,
                        transitionSpec = {
                            fadeIn() + slideInHorizontally() togetherWith fadeOut() + slideOutHorizontally()
                        },
                        label = "sensoryStep"
                    ) { activeStep ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.heightIn(min = 120.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    activeStep.third,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                activeStep.first,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                activeStep.second,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(
                            onClick = {
                                if (currentSensoryStep > 0) currentSensoryStep--
                            },
                            enabled = currentSensoryStep > 0,
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
                        ) {
                            Text("Previous")
                        }

                        Button(
                            onClick = {
                                if (currentSensoryStep < sensorySteps.lastIndex) {
                                    currentSensoryStep++
                                } else {
                                    currentSensoryStep = 0 // reset
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(if (currentSensoryStep == sensorySteps.lastIndex) "Start Over" else "Next Step")
                        }
                    }
                }
            }

            // Quick Actions Links
            Text(
                "Immediate Support Practices",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.Start)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToBreathe() },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🌊", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Breathe Now",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "Guided deep breath",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToCoach() },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("💬", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Talk to Coach",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "Empathetic chat bots",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
