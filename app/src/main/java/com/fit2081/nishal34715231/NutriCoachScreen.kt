// File: app/src/main/java/com/fit2081/nishal34715231/NutriCoachScreen.kt
package com.fit2081.nishal34715231

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.fit2081.nishal34715231.data.Fruit
import com.fit2081.nishal34715231.data.NutriCoachTip
import com.fit2081.nishal34715231.viewmodel.NutriCoachViewModel
import com.fit2081.nishal34715231.viewmodel.UiState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// The lime green color used throughout the app
val LimeGreen = Color(0xFF32CD32)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutriCoachScreen(navController: NavHostController, userId: String) {
    val nutriCoachViewModel: NutriCoachViewModel = viewModel()
    val currentPatient by nutriCoachViewModel.currentPatient.observeAsState()
    val searchedFruit by nutriCoachViewModel.searchedFruit.observeAsState()
    val currentAiResponse by nutriCoachViewModel.currentAiResponse.observeAsState()
    val allUserTips by nutriCoachViewModel.allUserTips.observeAsState(emptyList())
    val aiResponseType by nutriCoachViewModel.aiResponseType.observeAsState("")

    val fruitSearchState by nutriCoachViewModel.fruitSearchState.collectAsState()
    val aiResponseState by nutriCoachViewModel.aiResponseState.collectAsState()

    // Local state
    var fruitSearchQuery by remember { mutableStateOf("") }
    var showTipsModal by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    // Load data when the screen initializes
    LaunchedEffect(userId) {
        nutriCoachViewModel.loadPatientById(userId)
        nutriCoachViewModel.loadAllTipsForUser(userId)
    }

    val darkBackgroundColor = Color.Black
    val primaryTextColor = Color.White
    val accentColor = LimeGreen

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "NutriCoach",
                        fontFamily = FunnelDisplay,
                        fontWeight = FontWeight.Bold,
                        color = primaryTextColor
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = darkBackgroundColor,
                    titleContentColor = primaryTextColor
                )
            )
        },
        containerColor = darkBackgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(darkBackgroundColor)
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section divider
            SectionHeader(
                title = "Fruit Facts",
                backgroundColor = accentColor,
                textColor = Color.Black
            )

            // Fruit Section (top 50% of screen)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Show different content based on the fruit score
                currentPatient?.let { patient ->
                    val fruitScore = patient.fruitScore ?: 0f

                    if (fruitScore < 5f) {
                        // Low fruit score - show search and results
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                "Your fruit score is ${String.format("%.1f", fruitScore)} out of 10. Let's improve it!",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontFamily = FunnelDisplay,
                                    color = primaryTextColor
                                ),
                                textAlign = TextAlign.Center
                            )

                            // Fruit search section
                            OutlinedTextField(
                                value = fruitSearchQuery,
                                onValueChange = { fruitSearchQuery = it },
                                label = {
                                    Text("Search for a fruit",
                                        color = primaryTextColor.copy(alpha = 0.7f)
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = accentColor,
                                    unfocusedBorderColor = primaryTextColor.copy(alpha = 0.5f),
                                    focusedTextColor = primaryTextColor,
                                    unfocusedTextColor = primaryTextColor
                                ),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                        if (fruitSearchQuery.isNotEmpty()) {
                                            nutriCoachViewModel.searchFruitByName(fruitSearchQuery)
                                            focusManager.clearFocus()
                                        }
                                    }
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Button(
                                onClick = {
                                    if (fruitSearchQuery.isNotEmpty()) {
                                        nutriCoachViewModel.searchFruitByName(fruitSearchQuery)
                                        focusManager.clearFocus()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = accentColor,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth(0.8f)
                            ) {
                                Text(
                                    "Search",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontFamily = FunnelDisplay,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            // Show loading, error, or results
                            when (fruitSearchState) {
                                UiState.LOADING -> {
                                    CircularProgressIndicator(color = accentColor)
                                }
                                UiState.ERROR -> {
                                    Text(
                                        "Fruit not found. Try another one like 'apple', 'banana', or 'strawberry'.",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color.Red.copy(alpha = 0.8f),
                                            fontFamily = FunnelDisplay
                                        ),
                                        textAlign = TextAlign.Center
                                    )
                                }
                                UiState.SUCCESS -> {
                                    searchedFruit?.let { fruit ->
                                        FruitInfoCard(fruit = fruit, accentColor = accentColor, textColor = primaryTextColor)
                                    }
                                }
                                else -> { /* Idle state - nothing to show */ }
                            }
                        }
                    } else {
                        // Optimal fruit score - show congratulations message
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                "Your fruit score is ${String.format("%.1f", fruitScore)} out of 10. Great job!",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = FunnelDisplay,
                                    color = accentColor,
                                    fontWeight = FontWeight.Bold
                                ),
                                textAlign = TextAlign.Center
                            )

                            // Show optimal fruit image
                            Image(
                                painter = rememberAsyncImagePainter("https://picsum.photos/id/103/400/300"),
                                contentDescription = "Healthy fruits",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(16.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Text(
                                "Keep up the good work with your fruit intake!",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontFamily = FunnelDisplay,
                                    color = primaryTextColor
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } ?: run {
                    // Loading patient data
                    CircularProgressIndicator(color = accentColor)
                }
            }

            // Section divider
            SectionHeader(
                title = "Personalized Tips",
                backgroundColor = accentColor,
                textColor = Color.Black
            )

            // GenAI Section (bottom 50% of screen)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // GenAI response buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    if (currentAiResponse != null) {
                                        nutriCoachViewModel.clearCurrentAiResponse()
                                    }
                                    nutriCoachViewModel.generateMotivationalTip(userId)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = accentColor,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).padding(end = 8.dp)
                        ) {
                            Text(
                                "I need motivation",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = FunnelDisplay,
                                    fontWeight = FontWeight.Bold
                                ),
                                textAlign = TextAlign.Center
                            )
                        }

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    if (currentAiResponse != null) {
                                        nutriCoachViewModel.clearCurrentAiResponse()
                                    }
                                    nutriCoachViewModel.generateFoodTip(userId)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = accentColor,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).padding(start = 8.dp)
                        ) {
                            Text(
                                "Tell me a fun food tip!",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = FunnelDisplay,
                                    fontWeight = FontWeight.Bold
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // GenAI response display area
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.DarkGray.copy(alpha = 0.5f))
                            .border(
                                width = 1.dp,
                                color = accentColor,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        when (aiResponseState) {
                            UiState.LOADING -> {
                                CircularProgressIndicator(color = accentColor)
                            }
                            UiState.ERROR -> {
                                Text(
                                    "Sorry, I couldn't generate a tip. Please try again.",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.Red.copy(alpha = 0.8f),
                                        fontFamily = FunnelDisplay
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                            UiState.SUCCESS -> {
                                currentAiResponse?.let { response ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            if (aiResponseType == "MOTIVATION") "Motivation" else "Food Tip",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontFamily = FunnelDisplay,
                                                color = accentColor,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            textAlign = TextAlign.Center
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            response,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontFamily = FunnelDisplay,
                                                color = primaryTextColor
                                            ),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                            else -> {
                                Text(
                                    "Click one of the buttons above to get personalized tips based on your nutrition profile.",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = primaryTextColor.copy(alpha = 0.7f),
                                        fontFamily = FunnelDisplay
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    // Button to show all previous tips
                    Button(
                        onClick = { showTipsModal = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.DarkGray,
                            contentColor = primaryTextColor
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Text(
                            "Show all responses (${allUserTips.size})",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FunnelDisplay,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
        }
    }

    // Modal to show all previous tips
    if (showTipsModal) {
        TipsHistoryModal(
            tips = allUserTips,
            onDismiss = { showTipsModal = false },
            textColor = primaryTextColor,
            accentColor = accentColor
        )
    }
}

@Composable
fun SectionHeader(title: String, backgroundColor: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, shape = RoundedCornerShape(8.dp))
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FunnelDisplay,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        )
    }
}

@Composable
fun FruitInfoCard(fruit: Fruit, accentColor: Color, textColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.DarkGray.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                fruit.name,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FunnelDisplay,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            )

            Divider(color = accentColor.copy(alpha = 0.5f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Family: ${fruit.family}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FunnelDisplay,
                        color = textColor
                    )
                )
                Text(
                    "Genus: ${fruit.genus}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FunnelDisplay,
                        color = textColor
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Nutritional Information (per 100g):",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = FunnelDisplay,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            )

            NutritionTable(fruit = fruit, textColor = textColor)
        }
    }
}

@Composable
fun NutritionTable(fruit: Fruit, textColor: Color) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        NutritionRow("Calories", "${fruit.nutritions.calories} kcal", textColor)
        NutritionRow("Carbohydrates", "${fruit.nutritions.carbohydrates}g", textColor)
        NutritionRow("Protein", "${fruit.nutritions.protein}g", textColor)
        NutritionRow("Fat", "${fruit.nutritions.fat}g", textColor)
        NutritionRow("Sugar", "${fruit.nutritions.sugar}g", textColor)
    }
}

@Composable
fun NutritionRow(label: String, value: String, textColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FunnelDisplay,
                color = textColor.copy(alpha = 0.8f)
            )
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FunnelDisplay,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
        )
    }
}

@Composable
fun TipsHistoryModal(
    tips: List<NutriCoachTip>,
    onDismiss: () -> Unit,
    textColor: Color,
    accentColor: Color
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Your Nutrition Tips History",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FunnelDisplay,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                    )

                    IconButton(onClick = onDismiss) {
                        Text(
                            "X",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FunnelDisplay,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        )
                    }
                }

                Divider(color = accentColor.copy(alpha = 0.5f))

                // Tips list
                if (tips.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No tips generated yet. Click on one of the buttons to get your first tip!",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FunnelDisplay,
                                color = textColor.copy(alpha = 0.7f)
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        tips.forEach { tip ->
                            TipItem(
                                tip = tip,
                                dateFormat = dateFormat,
                                textColor = textColor,
                                accentColor = accentColor
                            )
                        }
                    }
                }

                // Close button
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text(
                        "Close",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FunnelDisplay,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun TipItem(
    tip: NutriCoachTip,
    dateFormat: SimpleDateFormat,
    textColor: Color,
    accentColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.DarkGray.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header with type and date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    if (tip.tipType == "MOTIVATION") "Motivation" else "Food Tip",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontFamily = FunnelDisplay,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                )

                Text(
                    dateFormat.format(tip.createdAt),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FunnelDisplay,
                        color = textColor.copy(alpha = 0.6f)
                    )
                )
            }

            Divider(color = accentColor.copy(alpha = 0.3f))

            // Tip content
            Text(
                tip.tipContent,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FunnelDisplay,
                    color = textColor
                )
            )
        }
    }
}