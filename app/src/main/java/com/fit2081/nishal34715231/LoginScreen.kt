// File: app/src/main/java/com/fit2081/nishal34715231/LoginScreen.kt
package com.fit2081.nishal34715231

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.fit2081.nishal34715231.viewmodel.AuthResult
import com.fit2081.nishal34715231.viewmodel.FoodIntakeViewModel // Import FoodIntakeViewModel
import com.fit2081.nishal34715231.viewmodel.PatientViewModel
import kotlinx.coroutines.launch // Import for launching coroutines

private enum class LoginMode {
    LOGIN, REGISTER_CLAIM
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavHostController) {
    val context = LocalContext.current
    val patientViewModel: PatientViewModel = viewModel()
    val foodIntakeViewModel: FoodIntakeViewModel = viewModel() // Instance of FoodIntakeViewModel
    val scope = rememberCoroutineScope() // Coroutine scope for launching suspend functions

    var currentMode by remember { mutableStateOf(LoginMode.LOGIN) }
    var selectedUserId by remember { mutableStateOf("") }
    var isUserIdDropdownExpanded by remember { mutableStateOf(false) }
    var phoneNumberInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }

    val userIdsFromDb by patientViewModel.allPatientUserIds.observeAsState(initial = emptyList<String>())
    val authResult by patientViewModel.authResult.observeAsState()

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(authResult) {
        isLoading = false
        when (val result = authResult) {
            is AuthResult.Success -> {
                errorMessage = null
                val prefs = context.getSharedPreferences("NutriCoachPrefs", Context.MODE_PRIVATE)
                prefs.edit().putString("LOGGED_IN_USER_ID", result.patient.userId).apply()

                // Check questionnaire status from database and navigate
                scope.launch {
                    val foodIntakeRecord = foodIntakeViewModel.getFoodIntakeDataOnce(result.patient.userId)
                    if (foodIntakeRecord != null) {
                        // Questionnaire data exists, navigate to home
                        navController.navigate("home/${result.patient.userId}") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    } else {
                        // No questionnaire data, navigate to questionnaire screen
                        navController.navigate("questionnaire/${result.patient.userId}") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    }
                }
                patientViewModel.resetAuthResult()
            }
            is AuthResult.Error -> errorMessage = result.message
            is AuthResult.Loading -> {
                isLoading = true
                errorMessage = null
            }
            is AuthResult.Idle, null -> { /* Do nothing specific here */ }
        }
    }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = Color.Gray,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = Color.Gray,
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
    )
    val textStyleWithFont = TextStyle(fontFamily = FunnelDisplay)

    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (currentMode == LoginMode.LOGIN) "Login" else "Register / Claim Account",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontFamily = FunnelDisplay,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                // User ID Dropdown
                ExposedDropdownMenuBox(
                    expanded = isUserIdDropdownExpanded,
                    onExpandedChange = { isUserIdDropdownExpanded = !isUserIdDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedUserId.ifEmpty { "Select User ID" },
                        onValueChange = {},
                        label = { Text("User ID", fontFamily = FunnelDisplay) },
                        readOnly = true,
                        shape = RoundedCornerShape(18.dp),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isUserIdDropdownExpanded) },
                        colors = textFieldColors,
                        textStyle = textStyleWithFont,
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = isUserIdDropdownExpanded,
                        onDismissRequest = { isUserIdDropdownExpanded = false }
                    ) {
                        if (userIdsFromDb.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("Loading User IDs...", fontFamily = FunnelDisplay) },
                                onClick = { isUserIdDropdownExpanded = false }
                            )
                        } else {
                            userIdsFromDb.forEach { userIdValue ->
                                DropdownMenuItem(
                                    text = { Text(userIdValue, fontFamily = FunnelDisplay) },
                                    onClick = {
                                        selectedUserId = userIdValue
                                        isUserIdDropdownExpanded = false
                                        errorMessage = null
                                        patientViewModel.resetAuthResult()
                                    }
                                )
                            }
                        }
                    }
                }

                // Fields specific to Register/Claim mode
                if (currentMode == LoginMode.REGISTER_CLAIM) {
                    OutlinedTextField(
                        value = phoneNumberInput,
                        onValueChange = { newValue -> if (newValue.all { it.isDigit() }) phoneNumberInput = newValue },
                        label = { Text("Phone Number (from CSV)", fontFamily = FunnelDisplay) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = textFieldColors,
                        textStyle = textStyleWithFont,
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Your Name", fontFamily = FunnelDisplay) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = textFieldColors,
                        textStyle = textStyleWithFont,
                        singleLine = true
                    )
                }

                // Password Field
                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = { passwordInput = it },
                    label = { Text("Password", fontFamily = FunnelDisplay) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = textFieldColors,
                    textStyle = textStyleWithFont,
                    singleLine = true
                )

                // Confirm Password Field
                if (currentMode == LoginMode.REGISTER_CLAIM) {
                    OutlinedTextField(
                        value = confirmPasswordInput,
                        onValueChange = { confirmPasswordInput = it },
                        label = { Text("Confirm Password", fontFamily = FunnelDisplay) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = textFieldColors,
                        textStyle = textStyleWithFont,
                        singleLine = true
                    )
                }

                errorMessage?.let {
                    Text(
                        text = it,
                        color = SalmonRed,
                        fontFamily = FunnelDisplay,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(top = 8.dp))
                }

                // Action Button
                Button(
                    onClick = {
                        errorMessage = null
                        patientViewModel.resetAuthResult()
                        if (selectedUserId.isEmpty()) {
                            errorMessage = "Please select a User ID."
                            return@Button
                        }
                        if (passwordInput.isEmpty()) {
                            errorMessage = "Password cannot be empty."
                            return@Button
                        }
                        if (currentMode == LoginMode.LOGIN) {
                            patientViewModel.login(selectedUserId, passwordInput)
                        } else {
                            if (phoneNumberInput.isEmpty()) {
                                errorMessage = "Phone number cannot be empty for registration."
                                return@Button
                            }
                            if (nameInput.isEmpty()) {
                                errorMessage = "Name cannot be empty for registration."
                                return@Button
                            }
                            if (passwordInput.length < 6) {
                                errorMessage = "Password must be at least 6 characters."
                                return@Button
                            }
                            if (passwordInput != confirmPasswordInput) {
                                errorMessage = "Passwords do not match."
                                return@Button
                            }
                            patientViewModel.claimAccount(selectedUserId, phoneNumberInput, nameInput, passwordInput)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .padding(top = 20.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = LimeGreen
                    ),
                    enabled = !isLoading
                ) {
                    Text(
                        text = if (currentMode == LoginMode.LOGIN) "Continue" else "Register & Login",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FunnelDisplay,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // Toggle mode button
                TextButton(
                    onClick = {
                        currentMode = if (currentMode == LoginMode.LOGIN) LoginMode.REGISTER_CLAIM else LoginMode.LOGIN
                        errorMessage = null
                        patientViewModel.resetAuthResult()
                        phoneNumberInput = ""
                        nameInput = ""
                        passwordInput = ""
                        confirmPasswordInput = ""
                    },
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    Text(
                        text = if (currentMode == LoginMode.LOGIN) "New user? Register/Claim Account" else "Already registered? Login",
                        fontFamily = FunnelDisplay,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
