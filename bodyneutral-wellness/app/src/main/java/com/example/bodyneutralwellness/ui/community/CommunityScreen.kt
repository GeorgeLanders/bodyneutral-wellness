package com.example.bodyneutralwellness.ui.community

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
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ThumbUp
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
import com.example.bodyneutralwellness.data.CommunityPost
import com.example.bodyneutralwellness.data.WellnessPreferences

private val preloadedPosts = listOf(
    CommunityPost(
        id = "mock_1",
        category = "Body Image",
        text = "Felt really insecure in my swimsuit today, but then I remembered how much I love floating in the water. I chose joy over self-criticism today! 🌊❤️",
        author = "Sarah M.",
        likes = 12,
        claps = 8,
        time = "2h ago"
    ),
    CommunityPost(
        id = "mock_2",
        category = "Nourishment",
        text = "Tried the Avocado Power Toast recipe! Adding nutritional yeast and chili flakes. It's so satisfying to eat to feed my cells rather than starve them.",
        author = "Elena R.",
        likes = 19,
        claps = 15,
        time = "4h ago"
    ),
    CommunityPost(
        id = "mock_3",
        category = "Movement",
        text = "Did the Seated Joyful Stretch video this morning. My joints feel looser and more relaxed now. Highly recommend for anyone who wants gentle movement without pressure.",
        author = "Jordan K.",
        likes = 25,
        claps = 20,
        time = "Yesterday"
    ),
    CommunityPost(
        id = "mock_4",
        category = "General",
        text = "I'm here for weight management, but I appreciate that the app focuses on habits, strength, food, sleep, and consistency instead of shame.",
        author = "Marcus L.",
        likes = 42,
        claps = 30,
        time = "2d ago"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs = remember { WellnessPreferences(context) }
    var selectedCategory by remember { mutableStateOf("All") }

    var postText by remember { mutableStateOf("") }
    var selectedPostCategory by remember { mutableStateOf("General") }

    // Combine user custom posts and preloaded posts
    var userPosts by remember { mutableStateOf(prefs.getCommunityPosts()) }
    val allPosts = remember(userPosts) { userPosts + preloadedPosts }

    val filteredPosts = if (selectedCategory == "All") {
        allPosts
    } else {
        allPosts.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    val categories = listOf("All", "General", "Movement", "Nourishment", "Body Image")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Community Circles",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp)
        ) {
            // Write a New Post Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Share Your Intention or Win 🌿",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        OutlinedTextField(
                            value = postText,
                            onValueChange = { postText = it },
                            placeholder = { Text("Write something kind or encouraging for the circle…") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 90.dp),
                            shape = RoundedCornerShape(16.dp),
                            maxLines = 4,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Category Picker for Post
                        Text(
                            text = "Post in Circle:",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("General", "Movement", "Nourishment", "Body Image").forEach { cat ->
                                val isCatSelected = selectedPostCategory == cat
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { selectedPostCategory = cat }
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isCatSelected) MaterialTheme.colorScheme.secondaryContainer
                                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ) {
                                    Text(
                                        text = cat,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isCatSelected) MaterialTheme.colorScheme.onSecondaryContainer
                                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                if (postText.isNotBlank()) {
                                    val name = prefs.userName.ifBlank { "Friend" }
                                    prefs.addCommunityPost(selectedPostCategory, postText, name)
                                    userPosts = prefs.getCommunityPosts()
                                    postText = ""
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            enabled = postText.isNotBlank()
                        ) {
                            Icon(Icons.Default.AddComment, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Post to Circle")
                        }
                    }
                }
            }

            // Topic Filter Chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat) }
                        )
                    }
                }
            }

            // Posts List
            items(filteredPosts) { post ->
                var likesCount by remember(post.id) { mutableIntStateOf(post.likes) }
                var clapsCount by remember(post.id) { mutableIntStateOf(post.claps) }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (post.author.isNotBlank()) post.author.first().uppercase() else "?",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        post.author,
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        post.time,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    )
                                }
                            }

                            // Category Tag
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                            ) {
                                Text(
                                    text = post.category,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = post.text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 24.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Interactive Reactions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Hearts
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        likesCount++
                                        if (!post.id.startsWith("mock_")) {
                                            prefs.updateCommunityPostReactions(post.id, likesCount, clapsCount)
                                        }
                                    }
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Favorite,
                                    contentDescription = "Heart",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    likesCount.toString(),
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Claps
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        clapsCount++
                                        if (!post.id.startsWith("mock_")) {
                                            prefs.updateCommunityPostReactions(post.id, likesCount, clapsCount)
                                        }
                                    }
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.ThumbUp,
                                    contentDescription = "Clap",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    clapsCount.toString(),
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
