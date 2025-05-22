package com.fit2081.nishal34715231

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
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
import androidx.navigation.NavHostController
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Insights Screen displaying the breakdown of user's food quality scores by category
 *
 * @param navController Navigation controller for screen navigation
 * @param userId The user ID passed from previous screen
 */
@Composable
fun InsightsScreen(navController: NavHostController, userId: String) {
    // Get the current context for assets access and sharing functionality
    val context = LocalContext.current

    // State to store user data
    val userData = remember { mutableStateMapOf<String, Float>() }
    var userGender by remember { mutableStateOf("") }
    var totalFoodScore by remember { mutableStateOf(0) }

    // Category totals
    val categoryTotals = mapOf(
        "Vegetables" to 10f,
        "Fruits" to 10f,
        "Grains & Cereals" to 10f,
        "Whole grains" to 10f,
        "Meat & Alternative" to 10f,
        "Dairy" to 10f,
        "Water" to 5f,
        "Unsaturated fats" to 10f,
        "Sodium" to 10f,
        "Sugar" to 10f,
        "Alcohol" to 5f,
        "Discretionary foods" to 10f
    )

    // Load the user data from CSV
    LaunchedEffect(Unit) {
        fun readUserDataFromAssets() {
            try {
                context.assets.open("user_data.csv").use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        // Read header line to get column indices
                        val header = reader.readLine().split(",")
                        val userIdIndex = header.indexOf("User_ID")
                        val genderIndex = header.indexOf("Sex")

                        // Map category names to their column indexes
                        val columnMappings = mapOf(
                            "Vegetables" to Pair(header.indexOf("VegetablesHEIFAscoreMale"), header.indexOf("VegetablesHEIFAscoreFemale")),
                            "Fruits" to Pair(header.indexOf("FruitHEIFAscoreMale"), header.indexOf("FruitHEIFAscoreFemale")),
                            "Grains & Cereals" to Pair(header.indexOf("GrainsandcerealsHEIFAscoreMale"), header.indexOf("GrainsandcerealsHEIFAscoreFemale")),
                            "Whole grains" to Pair(header.indexOf("WholegrainsHEIFAscoreMale"), header.indexOf("WholegrainsHEIFAscoreFemale")),
                            "Meat & Alternative" to Pair(header.indexOf("MeatandalternativesHEIFAscoreMale"), header.indexOf("MeatandalternativesHEIFAscoreFemale")),
                            "Dairy" to Pair(header.indexOf("DairyandalternativesHEIFAscoreMale"), header.indexOf("DairyandalternativesHEIFAscoreFemale")),
                            "Water" to Pair(header.indexOf("WaterHEIFAscoreMale"), header.indexOf("WaterHEIFAscoreFemale")),
                            "Unsaturated fats" to Pair(header.indexOf("UnsaturatedFatHEIFAscoreMale"), header.indexOf("UnsaturatedFatHEIFAscoreFemale")),
                            "Sodium" to Pair(header.indexOf("SodiumHEIFAscoreMale"), header.indexOf("SodiumHEIFAscoreFemale")),
                            "Sugar" to Pair(header.indexOf("SugarHEIFAscoreMale"), header.indexOf("SugarHEIFAscoreFemale")),
                            "Alcohol" to Pair(header.indexOf("AlcoholHEIFAscoreMale"), header.indexOf("AlcoholHEIFAscoreFemale")),
                            "Discretionary foods" to Pair(header.indexOf("DiscretionaryHEIFAscoreMale"), header.indexOf("DiscretionaryHEIFAscoreFemale")),
                            "Total Score" to Pair(header.indexOf("HEIFAtotalscoreMale"), header.indexOf("HEIFAtotalscoreFemale"))
                        )

                        // Read data lines
                        var line = reader.readLine()
                        while (line != null) {
                            val parts = line.split(",")
                            if (parts.size > userIdIndex && parts[userIdIndex].trim() == userId) {
                                // Found our user, get gender
                                userGender = parts[genderIndex].trim()

                                // Get score for each category based on gender
                                columnMappings.forEach { (category, indices) ->
                                    val scoreIndex = if (userGender.equals("Male", ignoreCase = true)) indices.first else indices.second
                                    if (scoreIndex >= 0 && scoreIndex < parts.size) {
                                        val scoreStr = parts[scoreIndex].trim()
                                        try {
                                            val score = scoreStr.toFloat()
                                            userData[category] = score

                                            // If this is the total score, round it properly
                                            if (category == "Total Score") {
                                                totalFoodScore = Math.round(score)
                                            }
                                        } catch (e: Exception) {
                                            println("Error parsing score for $category: ${e.message}")
                                        }
                                    }
                                }
                                break // Found our user, stop searching
                            }
                            line = reader.readLine()
                        }
                    }
                }
            } catch (e: Exception) {
                println("Error reading CSV: ${e.message}")
            }
        }
        readUserDataFromAssets()
    }

    Scaffold(
        bottomBar = {
            InsightsBottomNavBar(navController = navController, userId = userId)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = "Food Score",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = FunnelDisplay,
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp
                ),
                modifier = Modifier.padding(vertical = 24.dp)
            )

            // Score categories
            categoryTotals.forEach { (category, totalValue) ->
                val score = userData[category] ?: 0f
                CategoryScoreBar(
                    category = category,
                    score = score.toInt(),
                    totalScore = totalValue.toInt(),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Total food quality score with larger bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total food quality score",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FunnelDisplay,
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    text = "$totalFoodScore/100",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FunnelDisplay,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            // Progress bar for total score
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .padding(vertical = 4.dp)
            ) {
                // Black line (goes full width) - placed FIRST so it's underneath
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .align(Alignment.CenterStart)
                        .background(Color.Black)
                )

                // End cap for line
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .align(Alignment.CenterEnd)
                        .background(Color.Black, shape = RoundedCornerShape(3.dp))
                )

                // Progress indicator - placed LAST so it's on top
                Box(
                    modifier = Modifier
                        .fillMaxWidth(totalFoodScore / 100f)
                        .height(24.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SalmonRed)
                )
            }

            // Buttons row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Share button
                Button(
                    onClick = {
                        // Share functionality
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "My Food Quality Score")
                            putExtra(Intent.EXTRA_TEXT, "My total food quality score is $totalFoodScore out of 100!")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share your score via"))
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),  // Less rounded corners
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = LimeGreen
                    )
                ) {
                    Text(
                        text = "Share with someone",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FunnelDisplay,
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center  // Center-aligned text
                    )
                }

                // Improve button
                Button(
                    onClick = {
                        // To be implemented later
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SalmonRed,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Improve my diet",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FunnelDisplay,
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * Displays a category score as a bar graph with label
 */
@Composable
fun CategoryScoreBar(
    category: String,
    score: Int,
    totalScore: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Category label with score
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = category,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = FunnelDisplay,
                    fontWeight = FontWeight.Medium
                )
            )

            Text(
                text = "$score/$totalScore",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FunnelDisplay
                )
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Bar graph
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .padding(vertical = 4.dp)
        ) {
            // Black line (goes full width) - placed FIRST so it's underneath
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.5.dp)
                    .align(Alignment.CenterStart)
                    .background(Color.Black)
            )

            // End cap for line
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .align(Alignment.CenterEnd)
                    .background(Color.Black, shape = RoundedCornerShape(2.dp))
            )

            // Progress indicator - placed LAST so it's on top
            val progress = if (totalScore > 0) score.toFloat() / totalScore else 0f
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(20.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SalmonRed)
            )
        }
    }
}


// Bottom Navigation Bar for the Insights screen

@Composable
fun InsightsBottomNavBar(navController: NavHostController, userId: String) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        containerColor = Color.Black
    ) {
        // Home Navigation Item
        NavigationBarItem(
            selected = false,
            onClick = {
                navController.navigate("home/$userId") {
                    // Pop up to the start destination to avoid building up a large stack
                    popUpTo("home/$userId") { inclusive = true }
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Home",
                    tint = LimeGreen.copy(alpha = 0.6f)
                )
            },
            label = {
                Text(
                    text = "Home",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FunnelDisplay,
                        fontWeight = FontWeight.Medium
                    ),
                    color = LimeGreen.copy(alpha = 0.6f)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Black,
                selectedIconColor = LimeGreen,
                selectedTextColor = LimeGreen,
                unselectedIconColor = LimeGreen.copy(alpha = 0.6f),
                unselectedTextColor = LimeGreen.copy(alpha = 0.6f)
            )
        )

        // Insights Navigation Item (selected)
        NavigationBarItem(
            selected = true,
            onClick = { /* Already on insights screen */ },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Insights",
                    tint = LimeGreen
                )
            },
            label = {
                Text(
                    text = "Insights",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FunnelDisplay,
                        fontWeight = FontWeight.Medium
                    ),
                    color = LimeGreen
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Black,
                selectedIconColor = LimeGreen,
                selectedTextColor = LimeGreen,
                unselectedIconColor = LimeGreen.copy(alpha = 0.6f),
                unselectedTextColor = LimeGreen.copy(alpha = 0.6f)
            )
        )

        // NutriCoach Navigation Item
        NavigationBarItem(
            selected = false,
            onClick = { /* Not implemented yet */ },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "NutriCoach",
                    tint = LimeGreen.copy(alpha = 0.6f)
                )
            },
            label = {
                Text(
                    text = "NutriCoach",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FunnelDisplay,
                        fontWeight = FontWeight.Medium
                    ),
                    color = LimeGreen.copy(alpha = 0.6f)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Black,
                selectedIconColor = LimeGreen,
                selectedTextColor = LimeGreen,
                unselectedIconColor = LimeGreen.copy(alpha = 0.6f),
                unselectedTextColor = LimeGreen.copy(alpha = 0.6f)
            )
        )

        // Settings Navigation Item
        NavigationBarItem(
            selected = false,
            onClick = { /* Not implemented yet */ },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings",
                    tint = LimeGreen.copy(alpha = 0.6f)
                )
            },
            label = {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FunnelDisplay,
                        fontWeight = FontWeight.Medium
                    ),
                    color = LimeGreen.copy(alpha = 0.6f)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Black,
                selectedIconColor = LimeGreen,
                selectedTextColor = LimeGreen,
                unselectedIconColor = LimeGreen.copy(alpha = 0.6f),
                unselectedTextColor = LimeGreen.copy(alpha = 0.6f)
            )
        )
    }
}