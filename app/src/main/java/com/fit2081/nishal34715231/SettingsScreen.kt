package com.fit2081.nishal34715231

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.fit2081.nishal34715231.viewmodel.PatientViewModel
import com.fit2081.nishal34715231.utils.SessionManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController, userId: String) {
    val context = LocalContext.current
    val patientViewModel: PatientViewModel = viewModel()
    val scope = rememberCoroutineScope()

    // Observe the current patient data
    val currentPatientState by patientViewModel.currentPatient.observeAsState()

    // Load patient data when the screen is composed or userId changes
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            patientViewModel.loadCurrentPatientById(userId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontFamily = FunnelDisplay, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            SettingsBottomNavBar(navController = navController, userId = userId)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User Details Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LimeGreen)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Your Details",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FunnelDisplay,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    DetailRow(label = "Name:", value = currentPatientState?.name ?: "N/A")
                    DetailRow(label = "User ID:", value = currentPatientState?.userId ?: "N/A")
                    DetailRow(label = "Phone:", value = currentPatientState?.phoneNumber ?: "N/A")
                }
            }

            // App Settings Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SalmonRed)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "App Settings",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FunnelDisplay,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Logout Button
                    Button(
                        onClick = {
                            scope.launch {
                                try {
                                    // Clear user session using SessionManager
                                    SessionManager.clearUserSession(context)

                                    // Navigate to login screen and clear back stack
                                    navController.navigate("login") {
                                        // Clear the entire back stack so user can't go back
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                } catch (e: Exception) {
                                    // Handle error gracefully - maybe show a snackbar
                                    android.util.Log.e("SettingsScreen", "Logout failed", e)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(
                            Icons.Filled.ExitToApp,
                            contentDescription = "Logout Icon",
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text("Logout", fontFamily = FunnelDisplay, fontWeight = FontWeight.Bold)
                    }

                    // Clinician Login Button
                    Button(
                        onClick = {
                            navController.navigate("clinicianLogin")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(
                            Icons.Filled.Lock,
                            contentDescription = "Clinician Login Icon",
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text("Clinician Login", fontFamily = FunnelDisplay, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = FunnelDisplay,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.weight(0.3f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FunnelDisplay),
            modifier = Modifier.weight(0.7f)
        )
    }
}

@Composable
fun SettingsBottomNavBar(navController: NavHostController, userId: String) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth().height(80.dp),
        containerColor = Color.Black
    ) {
        // Home
        NavigationBarItem(
            selected = false,
            onClick = {
                navController.navigate("home/$userId") {
                    popUpTo("home/$userId") { inclusive = true }
                }
            },
            icon = {
                Icon(
                    Icons.Filled.Home,
                    contentDescription = "Home",
                    tint = LimeGreen.copy(alpha = 0.6f)
                )
            },
            label = {
                Text(
                    "Home",
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FunnelDisplay),
                    color = LimeGreen.copy(alpha = 0.6f)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent,
                selectedIconColor = LimeGreen,
                selectedTextColor = LimeGreen,
                unselectedIconColor = LimeGreen.copy(alpha = 0.6f),
                unselectedTextColor = LimeGreen.copy(alpha = 0.6f)
            )
        )
        // Insights
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("insights/$userId") },
            icon = {
                Icon(
                    Icons.Filled.Info,
                    contentDescription = "Insights",
                    tint = LimeGreen.copy(alpha = 0.6f)
                )
            },
            label = {
                Text(
                    "Insights",
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FunnelDisplay),
                    color = LimeGreen.copy(alpha = 0.6f)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent,
                selectedIconColor = LimeGreen,
                selectedTextColor = LimeGreen,
                unselectedIconColor = LimeGreen.copy(alpha = 0.6f),
                unselectedTextColor = LimeGreen.copy(alpha = 0.6f)
            )
        )
        // NutriCoach
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("nutricoach/$userId") },
            icon = {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = "NutriCoach",
                    tint = LimeGreen.copy(alpha = 0.6f)
                )
            },
            label = {
                Text(
                    "NutriCoach",
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FunnelDisplay),
                    color = LimeGreen.copy(alpha = 0.6f)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent,
                selectedIconColor = LimeGreen,
                selectedTextColor = LimeGreen,
                unselectedIconColor = LimeGreen.copy(alpha = 0.6f),
                unselectedTextColor = LimeGreen.copy(alpha = 0.6f)
            )
        )
        // Settings (Selected)
        NavigationBarItem(
            selected = true,
            onClick = { /* Already here */ },
            icon = {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = "Settings",
                    tint = LimeGreen
                )
            },
            label = {
                Text(
                    "Settings",
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FunnelDisplay),
                    color = LimeGreen
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent,
                selectedIconColor = LimeGreen,
                selectedTextColor = LimeGreen,
                unselectedIconColor = LimeGreen.copy(alpha = 0.6f),
                unselectedTextColor = LimeGreen.copy(alpha = 0.6f)
            )
        )
    }
}