package com.example.bodyneutralwellness.ui.gear

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Hiking
import androidx.compose.material.icons.outlined.Accessibility
import androidx.compose.material.icons.outlined.Weekend
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GearGuideScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Gear & Comfort", "Props Modifiers")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Comfort & Props Guide",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
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
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Sliding / Clickable Tabs
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (selectedTab == 0) {
                    Text(
                        text = "Your body deserves comfort. Here are practical tips designed for real bodies.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    GearCard(
                        icon = Icons.Outlined.FavoriteBorder,
                        title = "Managing Friction",
                        body = "Friction is natural when bodies move! To prevent discomfort, consider using anti-chafe balms (like Megababe Thigh Rescue or BodyGlide) on inner thighs, underarms, and under the chest before moving. Moisture-wicking fabrics also help tremendously."
                    )

                    GearCard(
                        icon = Icons.Outlined.Checkroom,
                        title = "Supportive Activewear",
                        body = "Look for high-waisted leggings that don't roll down. Brands like Superfit Hero, Girlfriend Collective, and Fabletics offer extended sizing with inclusive fits. Remember: your clothes should fit you — you shouldn't have to fit your clothes."
                    )

                    GearCard(
                        icon = Icons.Outlined.Hiking,
                        title = "Comfortable Footwear",
                        body = "Proper arch support is crucial. If standing is painful, try wide-fit sneakers like New Balance or Hoka. For our seated routines, comfortable socks with grip are all you need!"
                    )
                } else {
                    Text(
                        text = "Wellness is for everyone. Use standard household props to support your movement.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    GearCard(
                        icon = Icons.Outlined.Weekend, // Represents cushions / chairs
                        title = "Pillows & Cushions",
                        body = "A firm bed pillow or folded towel under the hips can ease lower back pressure during seated exercises. Placing a cushion behind your back when seated in a chair helps support the spine comfortably."
                    )

                    GearCard(
                        icon = Icons.Outlined.Accessibility, // Wall support
                        title = "Wall & Railing Stability",
                        body = "Standing balance exercises can be modified by placing one hand on a sturdy wall or the back of a couch. This removes the anxiety of falling and lets you focus on building lower-body strength safely."
                    )

                    GearCard(
                        icon = Icons.Outlined.FitnessCenter, // Blocks and straps
                        title = "Blocks & Straps (or Towels)",
                        body = "Yoga blocks bring the floor closer to you, reducing reach requirements. If you cannot reach your feet, loop a bathrobe strap, scarf, or hand towel around your foot to extend your reach comfortably without straining your neck."
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun GearCard(
    icon: ImageVector,
    title: String,
    body: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 24.sp
            )
        }
    }
}
