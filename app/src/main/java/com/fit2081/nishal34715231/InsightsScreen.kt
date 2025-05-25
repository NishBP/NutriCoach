// File: app/src/main/java/com/fit2081/nishal34715231/InsightsScreen.kt
package com.fit2081.nishal34715231

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState // For observing LiveData
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel // For obtaining ViewModel
import androidx.navigation.NavHostController
import com.fit2081.nishal34715231.data.Patient // Import Patient data class
import com.fit2081.nishal34715231.viewmodel.PatientViewModel // Import PatientViewModel
import java.util.Locale // For String.format


@Composable
fun InsightsScreen(navController: NavHostController, userId: String) {
    val context = LocalContext.current
    val patientViewModel: PatientViewModel = viewModel()

    // Observe the current patient data from the ViewModel
    val currentPatientState by patientViewModel.currentPatient.observeAsState()

    // Trigger loading of patient data when userId is available or changes
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            patientViewModel.loadCurrentPatientById(userId)
        }
    }

    // Define category names and their corresponding score accessors from Patient object
    // Also include the total possible score for each category.
    // This maps display names to (Patient.() -> Float?) and total score.
    val scoreCategories = remember(currentPatientState) {
        listOfNotNull(
            currentPatientState?.let { Triple("Vegetables", it.vegScore, 10f) },
            currentPatientState?.let { Triple("Fruits", it.fruitScore, 10f) },
            currentPatientState?.let { Triple("Grains & Cereals", it.grainScore, 10f) },
            // Assuming 'Whole grains' is part of 'Grains & Cereals' or a separate score if available in Patient entity
            // If 'Whole grains' is distinct and in Patient, add:
            // currentPatientState?.let { Triple("Whole grains", it.wholeGrainsScore, 10f) },
            currentPatientState?.let { Triple("Meat & Alternatives", it.meatFishPoultryScore, 10f) },
            currentPatientState?.let { Triple("Dairy & Alternatives", it.dairyScore, 10f) },
            currentPatientState?.let { Triple("Water", it.waterScore, 5f) }, // Max score for water is 5
            currentPatientState?.let { Triple("Unsaturated Fats", it.fatsOilsScore, 10f) },
            // 'Sodium', 'Sugar', 'Alcohol', 'Discretionary foods' are often inverse scores or handled differently.
            // For simplicity, if they are direct scores in your Patient entity and you want to display them:
            currentPatientState?.let { Triple("Added Sugar", it.addedSugarScore, 10f) }, // Assuming max 10
            currentPatientState?.let { Triple("Alcohol", it.alcoholScore, 5f) }        // Assuming max 5
            // Note: Your original CSV had "SodiumHEIFAscoreMale/Female", "SugarHEIFAscoreMale/Female", etc.
            // Ensure your Patient entity has corresponding fields like `sodiumScore`, `sugarScore`.
            // The total scores (10f, 5f) are based on common HEIFA guidelines but adjust if yours differ.
        )
    }

    val totalFoodScore = currentPatientState?.heifaTotalScore ?: 0f

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
            Text(
                text = "Food Score",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = FunnelDisplay,
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp
                ),
                modifier = Modifier.padding(vertical = 24.dp)
            )

            if (currentPatientState == null) {
                CircularProgressIndicator() // Show loading indicator while patient data is fetched
            } else {
                // Score categories
                scoreCategories.forEach { (categoryName, scoreValue, totalValue) ->
                    CategoryScoreBar(
                        category = categoryName,
                        score = scoreValue ?: 0f, // Use the Float score, default to 0f if null
                        totalScore = totalValue,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Total food quality score
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
                        // Format totalFoodScore to 2 decimal places
                        text = "${String.format(Locale.US, "%.2f", totalFoodScore)}/100.00",
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
                        .height(24.dp) // Increased height for better visibility
                        .padding(vertical = 4.dp)
                ) {
                    Box( // Background track for the progress bar
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp) // Make track thinner than progress
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.LightGray) // Or a theme-appropriate color
                            .align(Alignment.CenterStart)
                    )
                    Box( // Actual progress
                        modifier = Modifier
                            .fillMaxWidth(if (100f > 0) totalFoodScore / 100f else 0f)
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(SalmonRed) // Your theme color for progress
                            .align(Alignment.CenterStart)
                    )
                }


                // Buttons row (Share, Improve)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button( // Share button
                        onClick = {
                            val shareText = "My total food quality score is ${String.format(Locale.US, "%.2f", totalFoodScore)} out of 100.00!"
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, null)
                            context.startActivity(shareIntent)
                        },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = LimeGreen)
                    ) {
                        Text("Share with someone", style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FunnelDisplay, fontWeight = FontWeight.Bold), textAlign = TextAlign.Center)
                    }
                    Button( // Improve button
                        onClick = { /* Navigate to NutriCoach or other relevant screen */
                            navController.navigate("nutricoach/$userId") // Example navigation
                        },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SalmonRed, contentColor = Color.White)
                    ) {
                        Text("Improve my diet", style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FunnelDisplay, fontWeight = FontWeight.Bold), textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryScoreBar(
    category: String,
    score: Float, // Changed to Float
    totalScore: Float, // Changed to Float
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = category,
                style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FunnelDisplay, fontWeight = FontWeight.Medium)
            )
            // Format score and totalScore to 2 decimal places for display
            Text(
                text = "${String.format(Locale.US, "%.2f", score)}/${String.format(Locale.US, "%.2f", totalScore)}",
                style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FunnelDisplay)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box( // Progress bar container
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .padding(vertical = 4.dp)
        ) {
            Box( // Background track
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp) // Make track thinner
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color.LightGray)
                    .align(Alignment.CenterStart)
            )
            val progress = if (totalScore > 0f) score / totalScore else 0f
            Box( // Actual progress
                modifier = Modifier
                    .fillMaxWidth(progress.coerceIn(0f, 1f)) // Ensure progress is between 0 and 1
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(SalmonRed)
                    .align(Alignment.CenterStart)
            )
        }
    }
}

// Bottom Navigation Bar (InsightsBottomNavBar) - Assuming this is already correct from your files
@Composable
fun InsightsBottomNavBar(navController: NavHostController, userId: String) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth().height(80.dp), // Adjusted height
        containerColor = Color.Black
    ) {
        // Home
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("home/$userId") { popUpTo("home/$userId") { inclusive = true } } },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home", tint = LimeGreen.copy(alpha = 0.6f)) },
            label = { Text("Home", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FunnelDisplay), color = LimeGreen.copy(alpha = 0.6f)) },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent, selectedIconColor = LimeGreen, selectedTextColor = LimeGreen, unselectedIconColor = LimeGreen.copy(alpha = 0.6f), unselectedTextColor = LimeGreen.copy(alpha = 0.6f))
        )
        // Insights (Selected)
        NavigationBarItem(
            selected = true,
            onClick = { /* Already here */ },
            icon = { Icon(Icons.Filled.Info, contentDescription = "Insights", tint = LimeGreen) },
            label = { Text("Insights", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FunnelDisplay), color = LimeGreen) },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent, selectedIconColor = LimeGreen, selectedTextColor = LimeGreen, unselectedIconColor = LimeGreen.copy(alpha = 0.6f), unselectedTextColor = LimeGreen.copy(alpha = 0.6f))
        )
        // NutriCoach
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("nutricoach/$userId") /* Placeholder for navigation */ },
            icon = { Icon(Icons.Filled.Person, contentDescription = "NutriCoach", tint = LimeGreen.copy(alpha = 0.6f)) },
            label = { Text("NutriCoach", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FunnelDisplay), color = LimeGreen.copy(alpha = 0.6f)) },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent, selectedIconColor = LimeGreen, selectedTextColor = LimeGreen, unselectedIconColor = LimeGreen.copy(alpha = 0.6f), unselectedTextColor = LimeGreen.copy(alpha = 0.6f))
        )
        // Settings
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("settings/$userId") /* Placeholder for navigation */ },
            icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = LimeGreen.copy(alpha = 0.6f)) },
            label = { Text("Settings", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FunnelDisplay), color = LimeGreen.copy(alpha = 0.6f)) },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent, selectedIconColor = LimeGreen, selectedTextColor = LimeGreen, unselectedIconColor = LimeGreen.copy(alpha = 0.6f), unselectedTextColor = LimeGreen.copy(alpha = 0.6f))
        )
    }
}
