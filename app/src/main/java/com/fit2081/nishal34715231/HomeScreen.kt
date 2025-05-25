// File: app/src/main/java/com/fit2081/nishal34715231/HomeScreen.kt
package com.fit2081.nishal34715231

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext // Keep for SharedPreferences if still needed for other things
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel // For obtaining ViewModel
import androidx.navigation.NavHostController
import com.fit2081.nishal34715231.viewmodel.PatientViewModel // Import PatientViewModel
import java.util.Locale // For String.format

@Composable
fun HomeScreen(navController: NavHostController, userId: String) {
    val patientViewModel: PatientViewModel = viewModel()

    // Observe the current patient data from the ViewModel
    val currentPatientState by patientViewModel.currentPatient.observeAsState()

    // State for the food quality score, initialized to "--"
    var foodQualityScoreDisplay by remember { mutableStateOf("--") }

    // Trigger loading of patient data when userId is available or changes
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            patientViewModel.loadCurrentPatientById(userId)
        }
    }

    // Update foodQualityScoreDisplay when currentPatientState changes
    LaunchedEffect(currentPatientState) {
        currentPatientState?.heifaTotalScore?.let { score ->
            foodQualityScoreDisplay = String.format(Locale.US, "%.2f", score)
        } ?: run {
            foodQualityScoreDisplay = "--" // Reset if patient data is null
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(navController = navController, userId = userId)
        }
    ) { paddingValues ->
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
                colors = CardDefaults.cardColors(containerColor = LimeGreen)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hello,",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontFamily = FunnelDisplay,
                                fontWeight = FontWeight.Normal,
                                fontSize = 32.sp
                            )
                        )
                        Text(
                            // Display patient's name if available, otherwise fallback to userId
                            text = currentPatientState?.name?.takeIf { it.isNotBlank() } ?: "user $userId",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontFamily = FunnelDisplay,
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp
                            )
                        )
                    }
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                        Text(
                            text = "You've already filled in your food intake, but you can change details here",
                            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FunnelDisplay, textAlign = TextAlign.End)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { navController.navigate("questionnaire/$userId") },
                            modifier = Modifier.align(Alignment.End).height(40.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                            border = null
                        ) {
                            Text("Edit", style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FunnelDisplay, fontWeight = FontWeight.Bold), color = Color.Black)
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
                colors = CardDefaults.cardColors(containerColor = SalmonRed)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Your food",
                            style = MaterialTheme.typography.headlineMedium.copy(fontFamily = FunnelDisplay, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 24.sp)
                        )
                        Text(
                            text = "quality score",
                            style = MaterialTheme.typography.headlineMedium.copy(fontFamily = FunnelDisplay, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 24.sp)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.End) {
                        Text(
                            text = foodQualityScoreDisplay, // Use the formatted score
                            style = MaterialTheme.typography.headlineLarge.copy(fontFamily = FunnelDisplay, fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 44.sp)
                        )
                        Text(
                            text = "/100",
                            style = MaterialTheme.typography.headlineMedium.copy(fontFamily = FunnelDisplay, fontWeight = FontWeight.Normal, color = Color.White, fontSize = 20.sp),
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
                colors = CardDefaults.cardColors(containerColor = LimeGreen)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                    Text(
                        text = "what is the food quality score?",
                        style = MaterialTheme.typography.headlineMedium.copy(fontFamily = FunnelDisplay, fontWeight = FontWeight.Bold, fontSize = 32.sp)
                    )
                    Divider(color = Color.Black.copy(alpha = 0.2f), thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))
                    Text(
                        text = "Your Food Quality Score provides a snapshot of how well your eating patterns align with established food guidelines, helping you identify both strengths and opportunities for improvement in your diet. This personalized measurement considers various food groups including vegetables, fruits, whole grains, and proteins to give you practical insights for making healthier food choices.",
                        style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FunnelDisplay, lineHeight = 24.sp)
                    )
                }
            }
        }
    }
}

// Bottom Navigation Bar component (assuming this is correct from your existing files)
@Composable
fun BottomNavBar(navController: NavHostController, userId: String) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth().height(80.dp), // Adjusted height
        containerColor = Color.Black
    ) {
        // Home (Selected)
        NavigationBarItem(
            selected = true,
            onClick = { /* Already here */ },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home", tint = LimeGreen) },
            label = { Text("Home", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FunnelDisplay), color = LimeGreen) },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent, selectedIconColor = LimeGreen, selectedTextColor = LimeGreen, unselectedIconColor = LimeGreen.copy(alpha = 0.6f), unselectedTextColor = LimeGreen.copy(alpha = 0.6f))
        )
        // Insights
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("insights/$userId") },
            icon = { Icon(Icons.Filled.Info, contentDescription = "Insights", tint = LimeGreen.copy(alpha = 0.6f)) },
            label = { Text("Insights", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FunnelDisplay), color = LimeGreen.copy(alpha = 0.6f)) },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent, selectedIconColor = LimeGreen, selectedTextColor = LimeGreen, unselectedIconColor = LimeGreen.copy(alpha = 0.6f), unselectedTextColor = LimeGreen.copy(alpha = 0.6f))
        )
        // NutriCoach
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("nutricoach/$userId") },
            icon = { Icon(Icons.Filled.Person, contentDescription = "NutriCoach", tint = LimeGreen.copy(alpha = 0.6f)) },
            label = { Text("NutriCoach", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FunnelDisplay), color = LimeGreen.copy(alpha = 0.6f)) },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent, selectedIconColor = LimeGreen, selectedTextColor = LimeGreen, unselectedIconColor = LimeGreen.copy(alpha = 0.6f), unselectedTextColor = LimeGreen.copy(alpha = 0.6f))
        )
        // Settings
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("settings/$userId") },
            icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = LimeGreen.copy(alpha = 0.6f)) },
            label = { Text("Settings", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FunnelDisplay), color = LimeGreen.copy(alpha = 0.6f)) },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent, selectedIconColor = LimeGreen, selectedTextColor = LimeGreen, unselectedIconColor = LimeGreen.copy(alpha = 0.6f), unselectedTextColor = LimeGreen.copy(alpha = 0.6f))
        )
    }
}
