package com.fit2081.nishal34715231

import androidx.compose.foundation.layout.* // For layout composables like Column, Spacer
import androidx.compose.material3.*
import androidx.compose.runtime.* // For Compose states like remember, mutableStateOf, LaunchedEffect
import androidx.compose.ui.Alignment // For aligning elements
import androidx.compose.ui.Modifier // For styling and modifying composables
import androidx.compose.ui.unit.dp // For using density-independent pixels
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import androidx.compose.ui.platform.LocalContext
import java.io.BufferedReader
import java.io.InputStreamReader
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextFieldDefaults

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun LoginScreen(navController: NavHostController) {
    // Get the current context to access assets
    val context = LocalContext.current
    // State to store user data (UserID -> Phone Number) loaded from CSV
    val userData = remember { mutableStateMapOf<String, String>() }

    LaunchedEffect(Unit) {
        fun readUserDataFromAssets() {
            try {
                // Debug print to verify we're trying to read the file
                println("Attempting to read user_data.csv from assets")

                // List all files in assets to debug
                val assetFiles = context.assets.list("")
                println("Files in assets directory: ${assetFiles?.joinToString(", ")}")

                context.assets.open("user_data.csv").use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        // Skip header
                        reader.readLine()

                        // Read lines
                        var line = reader.readLine() // Read the first line
                        while (line != null) { // As long as the line is not null
                            val parts = line.split(",")
                            if (parts.size >= 2) {
                                val phoneNumber = parts[0].trim()
                                val userId = parts[1].trim()
                                userData[userId] = phoneNumber
                                println("Loaded: UserId=$userId, PhoneNumber=$phoneNumber")
                            }
                            line = reader.readLine() // Read the next line for the next iteration
                        }
                    }
                }

                // Print the map size to confirm data was loaded
                println("Total user records loaded: ${userData.size}")

            } catch (e: Exception) {
                println("Error reading CSV: ${e.message}")
                e.printStackTrace()
            }
        }

        readUserDataFromAssets() // Call the function to load data
    }

    // State for the selected User ID in the dropdown
    var selectedUserId by remember { mutableStateOf("") }
    // State to control the visibility of the User ID dropdown
    var expanded by remember { mutableStateOf(false) }
    // State for the entered phone number
    var phoneNumber by remember { mutableStateOf("") }
    // State for displaying any error message
    var errorMessage by remember { mutableStateOf("") }

    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center // Center all content in the Box
        ) {
            // Main content column - now centered in the Box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Login header
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontFamily = FunnelDisplay,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Dropdown for User ID
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    OutlinedTextField(
                        value = selectedUserId,
                        onValueChange = { /* User should select from dropdown */ },
                        label = { Text("User ID", fontFamily = FunnelDisplay) },
                        readOnly = true,
                        shape = RoundedCornerShape(18.dp),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        textStyle = LocalTextStyle.current.copy(fontFamily = FunnelDisplay),
                        modifier = Modifier.menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        val userIds = userData.keys.toList()

                        if (userIds.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("No users available", fontFamily = FunnelDisplay) },
                                onClick = { expanded = false }
                            )
                        } else {
                            userIds.forEach { userId ->
                                DropdownMenuItem(
                                    text = { Text(userId, fontFamily = FunnelDisplay) },
                                    onClick = {
                                        selectedUserId = userId
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Phone Number field
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number", fontFamily = FunnelDisplay) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    textStyle = LocalTextStyle.current.copy(fontFamily = FunnelDisplay)
                )

                // Error message
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color(0xFFff5757),
                        fontFamily = FunnelDisplay,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Button(
                    onClick = {
                        // Validation logic
                        if (selectedUserId.isEmpty()) {
                            errorMessage = "Please select a User ID."
                            return@Button
                        }
                        if (phoneNumber.isEmpty()) {
                            errorMessage = "Please enter your phone number."
                            return@Button
                        }

                        val registeredNumber = userData[selectedUserId]
                        if (registeredNumber == null || registeredNumber != phoneNumber) {
                            errorMessage = "Invalid User ID or Phone Number."
                        } else {
                            errorMessage = "" // Clear error message on successful validation
                            println("Login Successful for User ID: $selectedUserId")

                            // Check if user has already completed the questionnaire
                            val sharedPreferences = context.getSharedPreferences("NutriTrackerPrefs", android.content.Context.MODE_PRIVATE)
                            val userPrefix = "user_${selectedUserId}_"
                            val hasCompletedQuestionnaire = sharedPreferences.contains("${userPrefix}food_categories")

                            if (hasCompletedQuestionnaire) {
                                // User has already filled in questionnaire, go to home screen
                                navController.navigate("home/$selectedUserId") {
                                    // Clear back stack so user can't go back to login using back button
                                    popUpTo("welcome") { inclusive = false }
                                }
                            } else {
                                // User hasn't filled in questionnaire yet, go to questionnaire screen
                                navController.navigate("questionnaire/$selectedUserId")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .padding(top = 24.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF000000),
                        contentColor = Color(0xFFc1ff72)
                    )
                ) {
                    Text(
                        text = "Continue",
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