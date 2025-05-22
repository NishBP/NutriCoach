package com.fit2081.nishal34715231

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
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
 * Home Screen displaying user food quality score and information
 *
 * @param navController Navigation controller for screen navigation
 * @param userId The user ID passed from previous screen
 */
@Composable
fun HomeScreen(navController: NavHostController, userId: String) {
    // State to store user data from CSV
    val userData = remember { mutableStateMapOf<String, String>() }
    // State for the food quality score
    var foodQualityScore by remember { mutableStateOf("--") }

    // Get the current context to access assets and SharedPreferences
    val context = LocalContext.current

    // Read user data from CSV file and SharedPreferences when composable is created
    LaunchedEffect(Unit) {
        // Access the shared preferences to get saved questionnaire data
        val sharedPreferences = context.getSharedPreferences("NutriTrackerPrefs", android.content.Context.MODE_PRIVATE)

        // Function to read user data from assets
        fun readUserDataFromAssets() {
            try {
                // Open and read the CSV file
                context.assets.open("user_data.csv").use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        // Read header line to get column indices
                        val header = reader.readLine().split(",")
                        val userIdIndex = header.indexOf("User_ID")
                        val genderIndex = header.indexOf("Sex") // Still using "Sex" as column name in CSV
                        val maleScoreIndex = header.indexOf("HEIFAtotalscoreMale")
                        val femaleScoreIndex = header.indexOf("HEIFAtotalscoreFemale")

                        // Read subsequent lines
                        var line = reader.readLine()
                        while (line != null) {
                            val parts = line.split(",")
                            if (parts.size > maxOf(userIdIndex, genderIndex, maleScoreIndex, femaleScoreIndex)) {
                                val csvUserId = parts[userIdIndex].trim()

                                // If this line contains data for the current user
                                if (csvUserId == userId) {
                                    val gender = parts[genderIndex].trim()

                                    // Get appropriate score based on gender
                                    foodQualityScore = if (gender.equals("Male", ignoreCase = true)) {
                                        parts[maleScoreIndex].trim()
                                    } else {
                                        parts[femaleScoreIndex].trim()
                                    }

                                    // Format score to show as integer with proper rounding
                                    try {
                                        val scoreFloat = foodQualityScore.toFloat()
                                        // Use Math.round() for proper rounding instead of toInt()
                                        foodQualityScore = Math.round(scoreFloat).toString()
                                    } catch (e: Exception) {
                                        println("Error: ${e.message}")
                                    }

                                    break // Found our user, stop searching
                                }
                            }
                            line = reader.readLine()
                        }
                    }
                }
            } catch (e: Exception) {
                println("Error reading CSV: ${e.message}")
                e.printStackTrace()
            }
        }

        // Call function to load data
        readUserDataFromAssets()
    }

    // Main screen content
    Scaffold(
        bottomBar = {
            // Bottom Navigation Bar
            BottomNavBar(navController = navController, userId = userId)
        }
    ) { paddingValues ->
        // Main content column with padding from the scaffold
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // First section: Greeting and edit option
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = LimeGreen
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Greeting with user ID
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Hello,",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontFamily = FunnelDisplay,
                                fontWeight = FontWeight.Normal,
                                fontSize = 32.sp
                            )
                        )
                        Text(
                            text = "user $userId",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontFamily = FunnelDisplay,
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp
                            )
                        )
                    }

                    // Right side with message and edit button
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "You've already filled in your food intake, but you can change details here",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FunnelDisplay,
                                textAlign = TextAlign.End
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Edit button
                        OutlinedButton(
                            onClick = {
                                // Navigate back to questionnaire with userId parameter
                                navController.navigate("questionnaire/$userId")
                            },
                            modifier = Modifier
                                .align(Alignment.End)
                                .height(40.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White
                            ),
                            border = null
                        ) {
                            Text(
                                text = "Edit",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontFamily = FunnelDisplay,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            // Second section: Food Quality Score
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SalmonRed
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left side - "Your food quality score"
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Your food",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontFamily = FunnelDisplay,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 24.sp
                            )
                        )
                        Text(
                            text = "quality score",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontFamily = FunnelDisplay,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 24.sp
                            )
                        )
                    }

                    // Right side - Score with /100
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = foodQualityScore,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontFamily = FunnelDisplay,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 64.sp
                            )
                        )
                        Text(
                            text = "/ 100",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontFamily = FunnelDisplay,
                                fontWeight = FontWeight.Normal,
                                color = Color.White,
                                fontSize = 24.sp
                            ),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            // Third section: food quality score explanation
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = LimeGreen
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // Section title
                    Text(
                        text = "what is the food quality score?",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = FunnelDisplay,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp
                        )
                    )

                    Divider(
                        color = Color.Black.copy(alpha = 0.2f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    // Description text
                    Text(
                        text = "Your Food Quality Score provides a snapshot of how well your eating patterns align with established food guidelines, helping you identify both strengths and opportunities for improvement in your diet. This personalized measurement considers various food groups including vegetables, fruits, whole grains, and proteins to give you practical insights for making healthier food choices.",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FunnelDisplay,
                            lineHeight = 24.sp
                        )
                    )
                }
            }
        }
    }
}

// Bottom Navigation Bar component

@Composable
fun BottomNavBar(navController: NavHostController, userId: String) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        containerColor = Color.Black
    ) {
        // Home Navigation Item (selected)
        NavigationBarItem(
            selected = true,
            onClick = { /* Already on home screen */ },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Home",
                    tint = LimeGreen
                )
            },
            label = {
                Text(
                    text = "Home",
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

        // Insights Navigation Item
        NavigationBarItem(
            selected = false,
            onClick = {
                // Navigate to insights screen with userId
                navController.navigate("insights/$userId")
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Insights",
                    tint = LimeGreen.copy(alpha = 0.6f)
                )
            },
            label = {
                Text(
                    text = "Insights",
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


        // NutriCoach Navigation Item
        NavigationBarItem(
            selected = false,
            onClick = { /* Not implemented yet */ },
            icon = {
                // Placeholder - person icon
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
