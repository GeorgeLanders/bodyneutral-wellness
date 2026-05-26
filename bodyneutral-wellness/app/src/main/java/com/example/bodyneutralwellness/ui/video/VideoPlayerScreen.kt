package com.example.bodyneutralwellness.ui.video

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.ClosedCaptionDisabled
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.delay

private val subtitles = mapOf(
    "Box Breathing" to listOf(
        0L to "Welcome to Box Breathing. Sit comfortably and release shoulder tension.",
        4000L to "Let's begin: Inhale slowly for 4 seconds...",
        8000L to "Hold your breath for 4 seconds...",
        12000L to "Exhale completely for 4 seconds...",
        16000L to "Hold empty for 4 seconds...",
        20000L to "Inhale again, matching the rhythm...",
        24000L to "Excellent. Relax your jaw and repeat."
    ),
    "Body Scan Relaxation" to listOf(
        0L to "Welcome to your Body Scan. Close your eyes if that feels safe.",
        4000L to "Direct your attention to your feet. Feel them resting on the floor.",
        9000L to "Notice any sensations without trying to change them. You are safe here.",
        15000L to "Move your awareness up to your legs. Breathe relaxation into your thighs.",
        21000L to "Relax your shoulders, your face, and your chest. Let your body simply be."
    ),
    "Gentle Morning Stroll" to listOf(
        0L to "Let's take a gentle walk together. Find a comfortable pace.",
        4000L to "Move at a speed that feels good and nourishing for your joints.",
        9000L to "Roll your shoulders back. Breathe in the morning air.",
        15000L to "Your body is strong and carries you. Appreciate this simple movement today."
    ),
    "Gentle Neck Stretch" to listOf(
        0L to "Let's do a gentle neck stretch. Keep your shoulders relaxed and down.",
        4000L to "Slowly drop your right ear toward your right shoulder.",
        9000L to "Hold and breathe. Feel the gentle release of tension.",
        14000L to "Slowly return to center, and now drop your left ear toward your left shoulder.",
        19000L to "Breathe. You are doing exactly what your body needs."
    )
)

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerScreen(
    videoName: String,
    videoResId: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val videoUri = Uri.parse("android.resource://${context.packageName}/${videoResId}")

    var ccEnabled by remember { mutableStateOf(true) }
    var currentPosition by remember { mutableLongStateOf(0L) }

    // Manage setup & clean up memory automatically
    val exoPlayer = remember(videoUri) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUri))
            prepare()
            playWhenReady = true
        }
    }

    LaunchedEffect(exoPlayer) {
        while (true) {
            currentPosition = exoPlayer.currentPosition
            delay(250)
        }
    }

    DisposableEffect(videoUri) {
        onDispose {
            exoPlayer.release()
        }
    }

    val activeSubtitles = subtitles[videoName] ?: listOf(
        0L to "Welcome to this gentle exercise session.",
        5000L to "Honor your body's energy today. Modify any movement that doesn't feel comfortable.",
        13000L to "Breathe deeply, inhaling strength and exhaling expectations.",
        21000L to "You are doing beautifully just by showing up for yourself."
    )
    val activeText = activeSubtitles.lastOrNull { currentPosition >= it.first }?.second ?: ""

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black) // Dark background for video viewing
    ) {
        // Custom Top Bar for Video Player
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = videoName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
            IconButton(onClick = { ccEnabled = !ccEnabled }) {
                Icon(
                    imageVector = if (ccEnabled) Icons.Default.ClosedCaption else Icons.Default.ClosedCaptionDisabled,
                    contentDescription = "Toggle Captions",
                    tint = Color.White
                )
            }
        }

        // ExoPlayer Native View
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = true
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Closed Captions Overlay
            if (ccEnabled && activeText.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 60.dp) // padded above exoplayer controls bar
                        .background(Color.Black.copy(alpha = 0.75f), shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = activeText,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
