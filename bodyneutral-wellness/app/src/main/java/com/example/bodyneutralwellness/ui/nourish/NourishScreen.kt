package com.example.bodyneutralwellness.ui.nourish

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Check
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
import com.example.bodyneutralwellness.data.WellnessPreferences
import com.example.bodyneutralwellness.data.NourishLog
import com.example.bodyneutralwellness.data.NourishInsightsEngine
import com.example.bodyneutralwellness.theme.WellnessBackgroundBrush

enum class MealCategory { ALL, ENERGIZE, COMFORT, HYDRATE }

data class MealIdea(
    val emoji: String,
    val title: String,
    val description: String,
    val tip: String,
    val category: MealCategory
)

val mealIdeas = listOf(
    MealIdea(
        "🥑", "Avocado Power Toast",
        "Whole grain toast topped with mashed avocado, cherry tomatoes, and a sprinkle of everything seasoning.",
        "Avocado adds healthy fats that help your body absorb vitamins A, D, E, and K.",
        MealCategory.ENERGIZE
    ),
    MealIdea(
        "🥣", "Warm Oatmeal Bowl",
        "Creamy oats topped with banana slices, a drizzle of honey, and a handful of walnuts.",
        "Oats release energy slowly throughout the morning, keeping you fueled without a crash.",
        MealCategory.COMFORT
    ),
    MealIdea(
        "🥗", "Rainbow Nourishment Bowl",
        "Brown rice base with roasted sweet potato, edamame, shredded carrots, and tahini dressing.",
        "Eating a variety of colors means a variety of nutrients — your body loves diversity.",
        MealCategory.ENERGIZE
    ),
    MealIdea(
        "🍲", "Gentle Lentil Soup",
        "Red lentils simmered with turmeric, cumin, garlic, and a squeeze of lemon.",
        "Lentils are packed with iron and protein — nourishing your body from the inside out.",
        MealCategory.COMFORT
    ),
    MealIdea(
        "🫐", "Berry Smoothie Boost",
        "Frozen mixed berries, banana, spinach, Greek yogurt, and a splash of oat milk.",
        "Berries are rich in antioxidants that support your cells and reduce inflammation.",
        MealCategory.ENERGIZE
    ),
    MealIdea(
        "🍵", "Golden Turmeric Latte",
        "Warm oat milk with turmeric, cinnamon, a pinch of black pepper, and honey.",
        "Turmeric's curcumin is a natural anti-inflammatory — warmth and healing in a cup.",
        MealCategory.HYDRATE
    ),
    MealIdea(
        "🥒", "Cucumber Mint Water",
        "Chilled water infused with cucumber slices, fresh mint leaves, and a squeeze of lime.",
        "Infused water makes hydration feel like a treat, not a task.",
        MealCategory.HYDRATE
    ),
    MealIdea(
        "🍠", "Sweet Potato & Black Bean Tacos",
        "Roasted sweet potato and seasoned black beans in soft tortillas with avocado crema.",
        "Sweet potatoes are rich in beta-carotene, which your body converts to Vitamin A for eye and skin health.",
        MealCategory.COMFORT
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NourishScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs = remember { WellnessPreferences(context) }
    var selectedCategory by remember { mutableStateOf(MealCategory.ALL) }
    var hydrationCups by remember { mutableIntStateOf(prefs.hydrationCups) }

    // Mindful logging UI states
    var showLogDialog by remember { mutableStateOf(false) }
    var mealNameInput by remember { mutableStateOf("") }
    var hungerType by remember { mutableStateOf("Both (Fuel & Joy)") } // Fuel, Joy, Comfort, Both
    var tasteRating by remember { mutableFloatStateOf(3f) }
    var textureRating by remember { mutableFloatStateOf(3f) }
    var smellRating by remember { mutableFloatStateOf(3f) }
    var satisfactionRating by remember { mutableFloatStateOf(3f) }
    var sensationNote by remember { mutableStateOf("") }

    var logsList by remember { mutableStateOf(prefs.getNourishLogs()) }
    val nourishInsight = NourishInsightsEngine.insightFor(logsList, hydrationCups)

    val displayedMeals = if (selectedCategory == MealCategory.ALL) {
        mealIdeas
    } else {
        mealIdeas.filter { it.category == selectedCategory }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Mindful Nourishment",
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
                .background(WellnessBackgroundBrush())
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 48.dp)
        ) {
            // Mindful Eating Philosophy Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🌿", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Nourishment is about adding fuel, comfort, and joy to your body.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 22.sp
                            ),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Interactive Check-in log trigger
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "Mindful Savoring Check-in",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Tune into your body's senses while eating. Slow down and enjoy each bite.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { showLogDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Log Mindful Savoring Moment")
                        }
                    }
                }
            }

            // Mindful Logs Saved List
            if (logsList.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.55f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                nourishInsight.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                nourishInsight.body,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.86f),
                                lineHeight = 22.sp
                            )
                        }
                    }
                }

                item {
                    Text(
                        "Your Savoring Moments",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(logsList.take(3)) { log ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    log.mealName.ifEmpty { "Mindful Snack" },
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    log.hungerType,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                SensesTag("Taste", log.tasteRating)
                                SensesTag("Smell", log.smellRating)
                                SensesTag("Texture", log.textureRating)
                            }
                            if (log.sensationNote.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "“${log.sensationNote}”",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Category Filter Chips
            item {
                Text(
                    "Body-Neutral Nourishing Ideas",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedCategory == MealCategory.ALL,
                        onClick = { selectedCategory = MealCategory.ALL },
                        label = { Text("All") }
                    )
                    FilterChip(
                        selected = selectedCategory == MealCategory.ENERGIZE,
                        onClick = { selectedCategory = MealCategory.ENERGIZE },
                        label = { Text("⚡ Energize") }
                    )
                    FilterChip(
                        selected = selectedCategory == MealCategory.COMFORT,
                        onClick = { selectedCategory = MealCategory.COMFORT },
                        label = { Text("🫶 Comfort") }
                    )
                    FilterChip(
                        selected = selectedCategory == MealCategory.HYDRATE,
                        onClick = { selectedCategory = MealCategory.HYDRATE },
                        label = { Text("💧 Hydrate") }
                    )
                }
            }

            // Meal Idea Cards
            items(displayedMeals) { meal ->
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
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(meal.emoji, fontSize = 24.sp)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    meal.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    meal.category.name.lowercase().replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            meal.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 24.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("💡", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    meal.tip,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            }

            // Hydration Quick Tracker
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Hydration Today",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("💧", fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "$hydrationCups cups",
                                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    if (hydrationCups > 0) {
                                        hydrationCups--
                                        prefs.hydrationCups = hydrationCups
                                    }
                                },
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("-", style = MaterialTheme.typography.titleLarge)
                            }
                            Button(
                                onClick = {
                                    hydrationCups++
                                    prefs.hydrationCups = hydrationCups
                                    prefs.recordTodayActive()
                                },
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add cup")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Cup")
                            }
                        }
                    }
                }
            }
        }
    }

    // Mindful Savoring Dialog
    if (showLogDialog) {
        AlertDialog(
            onDismissRequest = { showLogDialog = false },
            title = {
                Text(
                    "Mindful Eat Check-in",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = mealNameInput,
                        onValueChange = { mealNameInput = it },
                        label = { Text("What are you eating / drinking?") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("What did you focus on?", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Fuel", "Comfort", "Joy", "Both").forEach { type ->
                            FilterChip(
                                selected = hungerType == type,
                                onClick = { hungerType = type },
                                label = { Text(type) }
                            )
                        }
                    }

                    SensorySlider("Taste 👅", tasteRating) { tasteRating = it }
                    SensorySlider("Texture 👄", textureRating) { textureRating = it }
                    SensorySlider("Smell 👃", smellRating) { smellRating = it }
                    SensorySlider("Satiety/Fullness 🫶", satisfactionRating) { satisfactionRating = it }

                    OutlinedTextField(
                        value = sensationNote,
                        onValueChange = { sensationNote = it },
                        label = { Text("Savoring thoughts / notes") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        prefs.addNourishLog(
                            mealName = mealNameInput,
                            hungerType = hungerType,
                            taste = tasteRating,
                            texture = textureRating,
                            smell = smellRating,
                            satisfaction = satisfactionRating,
                            note = sensationNote
                        )
                        logsList = prefs.getNourishLogs()
                        prefs.nourishmentCount = prefs.nourishmentCount + 1

                        // Reset fields
                        mealNameInput = ""
                        tasteRating = 3f
                        textureRating = 3f
                        smellRating = 3f
                        satisfactionRating = 3f
                        sensationNote = ""
                        showLogDialog = false
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Save Mindfully")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SensesTag(label: String, score: Float) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
    ) {
        Text(
            "$label: ${"★".repeat(score.toInt())}${"☆".repeat(5 - score.toInt())}",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SensorySlider(label: String, value: Float, onValueChange: (Float) -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("${value.toInt()} / 5", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 1f..5f,
            steps = 3,
            modifier = Modifier.height(28.dp)
        )
    }
}
