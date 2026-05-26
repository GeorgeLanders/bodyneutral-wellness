package com.example.bodyneutralwellness.ui.breathing

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bodyneutralwellness.data.WellnessPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreatheCustomizerScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs = remember { WellnessPreferences(context) }

    var name by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf("🌸") }
    val emojis = listOf("🌸", "🌊", "💫", "🌿", "✨", "🍃", "☁️")

    var inhale by remember { mutableFloatStateOf(4f) }
    var hold1 by remember { mutableFloatStateOf(4f) }
    var exhale by remember { mutableFloatStateOf(4f) }
    var hold2 by remember { mutableFloatStateOf(4f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Design Breathing Rhythm", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Intro
            Text(
                "Create a custom pace that matches your body's lung capacity and comfort. No strain, no force.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            // Pattern Name Input
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Name Your Rhythm", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text("e.g. My Morning Calm") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text("Select a Visual Symbol", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        emojis.forEach { emoji ->
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clickable { selectedEmoji = emoji }
                                    .background(
                                        color = if (selectedEmoji == emoji) MaterialTheme.colorScheme.primaryContainer
                                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(emoji, fontSize = 20.sp)
                            }
                        }
                    }
                }
            }

            // Duration Sliders Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Customize Durations (Seconds)", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)

                    // Inhale
                    SliderRow(
                        label = "1. Breathe In",
                        value = inhale,
                        onValueChange = { inhale = it },
                        range = 1f..10f
                    )

                    // Hold In
                    SliderRow(
                        label = "2. Hold Breath (In)",
                        value = hold1,
                        onValueChange = { hold1 = it },
                        range = 0f..10f
                    )

                    // Exhale
                    SliderRow(
                        label = "3. Breathe Out",
                        value = exhale,
                        onValueChange = { exhale = it },
                        range = 1f..10f
                    )

                    // Hold Out
                    SliderRow(
                        label = "4. Hold Empty (Out)",
                        value = hold2,
                        onValueChange = { hold2 = it },
                        range = 0f..10f
                    )
                }
            }

            // Save button
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        prefs.addCustomBreath(
                            name = name,
                            emoji = selectedEmoji,
                            inhale = inhale.toInt(),
                            hold1 = hold1.toInt(),
                            exhale = exhale.toInt(),
                            hold2 = hold2.toInt()
                        )
                        onBack()
                    }
                },
                enabled = name.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Save Breathing Rhythm", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
fun SliderRow(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    range: ClosedFloatingPointRange<Float>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
            Text(
                text = if (value.toInt() == 0) "Skip" else "${value.toInt()} seconds",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            modifier = Modifier.width(150.dp)
        )
    }
}
