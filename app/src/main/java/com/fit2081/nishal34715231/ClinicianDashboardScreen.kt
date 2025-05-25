// File: app/src/main/java/com/fit2081/nishal34715231/ClinicianDashboardScreen.kt
package com.fit2081.nishal34715231

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.fit2081.nishal34715231.data.Patient
import com.fit2081.nishal34715231.viewmodel.PatientViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClinicianDashboardScreen(navController: NavHostController) {
    val patientViewModel: PatientViewModel = viewModel()

    // Observe all patients from the ViewModel
    val allPatientsState by patientViewModel.allPatients.observeAsState(initial = emptyList())

    var averageMaleScore by remember { mutableStateOf(0f) }
    var averageFemaleScore by remember { mutableStateOf(0f) }
    var maleUserCount by remember { mutableStateOf(0) }
    var femaleUserCount by remember { mutableStateOf(0) }

    // Calculate averages when patient data changes
    LaunchedEffect(allPatientsState) {
        val maleScores = mutableListOf<Float>()
        val femaleScores = mutableListOf<Float>()

        allPatientsState.forEach { patient ->
            // Only count patients who have a valid HEIFA score
            patient.heifaTotalScore?.let { score ->
                when {
                    patient.sex.equals("Male", ignoreCase = true) -> maleScores.add(score)
                    patient.sex.equals("Female", ignoreCase = true) -> femaleScores.add(score)
                }
            }
        }

        maleUserCount = maleScores.size
        femaleUserCount = femaleScores.size

        averageMaleScore = if (maleScores.isNotEmpty()) maleScores.average().toFloat() else 0f
        averageFemaleScore = if (femaleScores.isNotEmpty()) femaleScores.average().toFloat() else 0f
    }

    val darkBackgroundColor = Color.Black
    val primaryTextColor = Color.White
    val accentColor = Color(0xFFC1FF72)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Clinician Dashboard",
                        fontFamily = FunnelDisplay, // Apply FunnelDisplay
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            if (allPatientsState.isEmpty()) { // Show loading if data is not yet available
                CircularProgressIndicator(color = accentColor)
            } else {
                StatCard(
                    title = "Average Male HEIFA Score",
                    value = String.format(Locale.US, "%.2f", averageMaleScore),
                    count = maleUserCount,
                    backgroundColor = Color.DarkGray.copy(alpha = 0.5f),
                    textColor = primaryTextColor,
                    accentColor = accentColor
                )

                StatCard(
                    title = "Average Female HEIFA Score",
                    value = String.format(Locale.US, "%.2f", averageFemaleScore),
                    count = femaleUserCount,
                    backgroundColor = Color.DarkGray.copy(alpha = 0.5f),
                    textColor = primaryTextColor,
                    accentColor = accentColor
                )

                // Debug information - can be removed in production
                Text(
                    text = "Total patients loaded: ${allPatientsState.size}",
                    color = primaryTextColor.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentColor,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    "Done",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = FunnelDisplay, // Apply FunnelDisplay
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    count: Int,
    backgroundColor: Color,
    textColor: Color,
    accentColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FunnelDisplay, // Apply FunnelDisplay
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = FunnelDisplay, // Apply FunnelDisplay
                    fontWeight = FontWeight.ExtraBold,
                    color = accentColor,
                    fontSize = 48.sp
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Based on $count users",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FunnelDisplay, // Apply FunnelDisplay
                    color = textColor.copy(alpha = 0.7f)
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}