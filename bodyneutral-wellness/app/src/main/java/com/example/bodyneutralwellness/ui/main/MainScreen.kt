package com.example.bodyneutralwellness.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import androidx.navigation3.runtime.NavKey
import com.example.bodyneutralwellness.R
import com.example.bodyneutralwellness.VideoPlayer
import com.example.bodyneutralwellness.GearGuide
import com.example.bodyneutralwellness.theme.WellnessBackgroundBrush

data class VideoItem(val name: String, val resId: Int, val isMindfulness: Boolean = false)

val videoList = listOf(
    VideoItem("Body Scan Relaxation", R.raw.body_scan_relaxation, isMindfulness = true),
    VideoItem("Box Breathing", R.raw.box_breathing, isMindfulness = true),
    VideoItem("Gentle Morning Stroll", R.raw.gentle_morning_stroll),
    VideoItem("Gentle Neck Stretch", R.raw.gentle_neck_stretch, isMindfulness = true),
    VideoItem("Mindful Breathing", R.raw.mindfull, isMindfulness = true),
    VideoItem("Relaxation Breath", R.raw.relaxation_breath, isMindfulness = true),
    VideoItem("Seated Arm Circles", R.raw.seated_arm_circles),
    VideoItem("Seated Leg Lifts", R.raw.seated_leg_lifts),
    VideoItem("Seated Torso Twist", R.raw.seated_torso_twist),
    VideoItem("Seated Forward Fold", R.raw.seated_forward_fold),
    VideoItem("Cat-Cow Stretch", R.raw.cat_cowstretch),
    VideoItem("Side Steps", R.raw.side_steps),
    VideoItem("Will Push", R.raw.will_push)
)

@Composable
fun MainScreen(
    onItemClick: (NavKey) -> Unit,
    modifier: Modifier = Modifier
) {
    var isBadBodyImageDay by remember { mutableStateOf(false) }

    val displayedVideos = if (isBadBodyImageDay) {
        videoList.filter { it.isMindfulness }
    } else {
        videoList
    }

    val affirmation = if (isBadBodyImageDay) {
        "It's okay to just exist today.\nYour worth is not determined by your body."
    } else {
        "Daily Affirmation:\nMy body deserves respect, rest, and joyful movement today."
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WellnessBackgroundBrush())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Toggle Switch Area
        Surface(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Bad Body Image Day Mode",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Switch(
                    checked = isBadBodyImageDay,
                    onCheckedChange = { isBadBodyImageDay = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        }

        // Affirmation Card
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isBadBodyImageDay) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Text(
                text = affirmation,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 32.sp
                ),
                color = if (isBadBodyImageDay) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(24.dp),
                textAlign = TextAlign.Center
            )
        }
        
        // Gear Guide Button
        OutlinedButton(
            onClick = { onItemClick(GearGuide) },
            modifier = Modifier.fillMaxWidth().height(56.dp).padding(bottom = 24.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Comfort & Gear Guide",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
        }

        Text(
            text = if (isBadBodyImageDay) "Gentle Mindful Content" else "Video Library",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(displayedVideos) { video ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            onItemClick(VideoPlayer(videoName = video.name, videoResId = video.resId))
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Play Icon Placeholder
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
                        Text(
                            text = video.name,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
