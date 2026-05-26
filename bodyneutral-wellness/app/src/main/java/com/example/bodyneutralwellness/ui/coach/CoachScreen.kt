package com.example.bodyneutralwellness.ui.coach

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import com.example.bodyneutralwellness.data.AiCoachContext
import com.example.bodyneutralwellness.data.AiCoachRepository
import com.example.bodyneutralwellness.data.TtsManager
import com.example.bodyneutralwellness.data.WellnessPreferences
import com.example.bodyneutralwellness.theme.WellnessBackgroundBrush

data class ChatMessage(val text: String, val isUser: Boolean, var isSpeaking: Boolean = false)

fun generateBotResponse(userText: String): String {
    val lower = userText.lowercase()
    return when {
        lower.contains("tired") || lower.contains("exhausted") || lower.contains("fatigue") || lower.contains("exhaustion") ->
            "Rest is not laziness — it's restoration. Your body deserves space to recharge. How about trying a gentle 30-second Neck Stretch or a deep Box Breathing rhythm from the Movement flow builder?"
        lower.contains("bad") || lower.contains("ugly") || lower.contains("hate") || lower.contains("body-image") || lower.contains("body image") || lower.contains("distress") ->
            "I hear you, and those feelings are real and heavy. Let's practice neutral breathing. Your worth is completely independent of how you view your body right now. You exist, and that is enough."
        lower.contains("exercise") || lower.contains("workout") || lower.contains("move") || lower.contains("stretch") ->
            "Movement is an act of appreciation for your body's capabilities, not a punishment. Check out the Movement Flow Builder — maybe assemble some Seated Arm Circles and a Gentle Morning Stroll?"
        lower.contains("lose weight") || lower.contains("weight loss") || lower.contains("weight management") ->
            "Weight management can be supported without shame. Start with repeatable habits: hydration, nourishing meals, sleep, stress care, and movement you can return to consistently."
        lower.contains("eat") || lower.contains("food") || lower.contains("diet") || lower.contains("hungry") || lower.contains("nourish") ->
            "Savoring food is a beautiful form of comfort. Check out the Mindful Savoring Log in the Nourish tab to rate your food's Taste, Texture, and Smell, and check in on physical vs. emotional cravings."
        lower.contains("sleep") || lower.contains("insomnia") || lower.contains("rest") ->
            "Sleep is your body's chance to renew itself. Try building a custom flow with Mindful Breathing and Box Breathing before closing your eyes tonight. Let the day slide away."
        lower.contains("sad") || lower.contains("depressed") || lower.contains("anxious") || lower.contains("stress") || lower.contains("anxiety") || lower.contains("panic") ->
            "You are safe here. Take a long, slow breath. If you feel panic building, try our 5-4-3-2-1 Sensory Grounding cards inside the SOS tab to anchor yourself in the present."
        lower.contains("happy") || lower.contains("good") || lower.contains("great") || lower.contains("amazing") ->
            " celebrate this beautiful feeling! Hold onto this positive energy. Celebrating your non-scale victories is a lovely form of self-kindness!"
        else ->
            "Thank you for sharing that with me. Your feelings are fully valid. Remember to offer yourself compassion today — your body is doing its best to carry you through life. What would feel most supportive right now?"
    }
}

private fun shouldOfferImmediateSupport(text: String): Boolean {
    val lower = text.lowercase()
    return listOf(
        "panic",
        "anxious",
        "anxiety",
        "stress",
        "overwhelmed",
        "distress",
        "hate",
        "ugly",
        "body image",
        "body-image",
        "depressed",
        "sad"
    ).any { it in lower }
}

@Composable
fun CoachScreen(
    modifier: Modifier = Modifier,
    onNavigateToSos: () -> Unit = {},
    onNavigateToBreathe: () -> Unit = {},
    onNavigateToJournal: () -> Unit = {}
) {
    val context = LocalContext.current
    val prefs = remember { WellnessPreferences(context) }
    val ttsManager = remember { TtsManager(context) }
    val aiCoachRepository = remember(prefs.aiCoachProxyUrl) { AiCoachRepository(prefs.aiCoachProxyUrl) }

    DisposableEffect(Unit) {
        onDispose {
            ttsManager.shutdown()
        }
    }

    var inputText by remember { mutableStateOf("") }
    var isThinking by remember { mutableStateOf(false) }
    var showSupportActions by remember { mutableStateOf(false) }
    var rememberedTopics by remember { mutableStateOf(prefs.getCoachTopics()) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val messages = remember { mutableStateListOf(
        ChatMessage("Hi! I'm your WellnessBot 🌿\n\nI'm here to support you with empathy and zero judgment. How is your body feeling today?", false)
    )}

    // Active speaking text
    var activelySpeakingMessage by remember { mutableStateOf<ChatMessage?>(null) }

    fun currentCoachContext() = AiCoachContext(
        userName = prefs.userName,
        goals = prefs.wellnessGoals,
        mobilityPreference = prefs.mobilityPreference,
        dailyIntention = prefs.dailyIntention,
        streakCount = prefs.streakCount,
        hydrationCups = prefs.hydrationCups,
        movementMinutes = prefs.movementMinutes,
        nourishmentCount = prefs.nourishmentCount,
        sleepHours = prefs.sleepHours
    )

    fun sendMessage(text: String) {
        val trimmedText = text.trim()
        if (trimmedText.isBlank() || isThinking) return

        messages.add(ChatMessage(trimmedText, true))
        prefs.rememberCoachTopic(trimmedText)
        rememberedTopics = prefs.getCoachTopics()
        showSupportActions = shouldOfferImmediateSupport(trimmedText)
        inputText = ""
        scope.launch {
            isThinking = true
            listState.animateScrollToItem(messages.size - 1)
            val response = aiCoachRepository.generateReply(trimmedText, currentCoachContext())
            messages.add(ChatMessage(response, false))
            isThinking = false
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WellnessBackgroundBrush())
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.padding(bottom = 12.dp, start = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🌿", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "AI Wellness Coach",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (prefs.aiCoachProxyUrl.isBlank()) "Offline support mode" else "Connected through private proxy",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Animated wave visualizer when speaking
            AnimatedVisibility(
                visible = activelySpeakingMessage != null,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Hearing, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(16.dp))
                    Text("Speaking...", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer)
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)

        // Chat area
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(messages) { msg ->
                ChatBubble(
                    message = msg,
                    isActivelySpeaking = msg == activelySpeakingMessage,
                    onSpeak = {
                        // Toggle or play
                        if (activelySpeakingMessage == msg) {
                            ttsManager.speak("", prefs.ttsRate, prefs.ttsPitch) // Stop speaking
                            activelySpeakingMessage = null
                        } else {
                            activelySpeakingMessage = msg
                            ttsManager.speak(msg.text, prefs.ttsRate, prefs.ttsPitch)
                        }
                    }
                )
            }
            if (isThinking) {
                item {
                    ChatBubble(
                        message = ChatMessage("Thinking through a gentle response...", false),
                        isActivelySpeaking = false
                    )
                }
            }
        }

        AnimatedVisibility(visible = rememberedTopics.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Remembered themes",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rememberedTopics.take(3).forEach { topic ->
                        AssistChip(
                            onClick = { sendMessage("I want support with $topic") },
                            label = { Text(topic) }
                        )
                    }
                }
            }
        }

        AnimatedVisibility(visible = showSupportActions) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Immediate support",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AssistChip(
                        onClick = onNavigateToSos,
                        label = { Text("SOS") },
                        modifier = Modifier.weight(1f)
                    )
                    AssistChip(
                        onClick = onNavigateToBreathe,
                        label = { Text("Breathe") },
                        modifier = Modifier.weight(1f)
                    )
                    AssistChip(
                        onClick = onNavigateToJournal,
                        label = { Text("Journal") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Quick-Reply Support prompts
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Need quick support?",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val prompts = listOf(
                    "Support body-distress 🫶",
                    "Comfort exhaustion 🌸",
                    "Help me ground stress 🌊"
                )
                prompts.forEach { prompt ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { sendMessage(prompt) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = prompt,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(10.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Input area
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 60.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Share how you're feeling...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                singleLine = false,
                maxLines = 3
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = { sendMessage(inputText) },
                enabled = !isThinking,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: ChatMessage,
    isActivelySpeaking: Boolean,
    onSpeak: (() -> Unit)? = null
) {
    val alignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    val bgColor = if (message.isUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (message.isUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    val shape = if (message.isUser) {
        RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)
    } else {
        RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp)
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Surface(
            color = bgColor,
            shape = shape,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor,
                    lineHeight = 26.sp
                )
                if (!message.isUser && onSpeak != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    IconButton(
                        onClick = onSpeak,
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.End)
                            .clip(CircleShape)
                            .background(
                                if (isActivelySpeaking) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                            )
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Speak reply",
                            tint = if (isActivelySpeaking) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
