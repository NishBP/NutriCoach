// File: app/src/main/java/com/fit2081/nishal34715231/ClinicianLoginScreen.kt
package com.fit2081.nishal34715231

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController


private const val CLINICIAN_KEY = "dollar-entry-apples"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClinicianLoginScreen(navController: NavHostController) {
    val context = LocalContext.current
    var enteredKey by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val darkBackgroundColor = Color.Black
    val primaryTextColor = Color.White
    val accentColor = LimeGreen

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Clinician Login",
                        fontFamily = FunnelDisplay,
                        fontWeight = FontWeight.Bold,
                        color = primaryTextColor
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = primaryTextColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = darkBackgroundColor,
                    titleContentColor = primaryTextColor,
                    navigationIconContentColor = primaryTextColor
                )
            )
        },
        containerColor = darkBackgroundColor // Sets the background for the Scaffold content area
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                // .background(darkBackgroundColor) // Already set by Scaffold's containerColor
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.8f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Enter Clinician Key",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontFamily = FunnelDisplay,
                        fontWeight = FontWeight.SemiBold,
                        color = primaryTextColor
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                OutlinedTextField(
                    value = enteredKey,
                    onValueChange = { enteredKey = it },
                    label = { Text("Clinician Key", fontFamily = FunnelDisplay, color = accentColor.copy(alpha = 0.7f)) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = accentColor,
                        unfocusedLabelColor = Color.Gray.copy(alpha = 0.7f),
                        cursorColor = accentColor,
                        focusedTextColor = primaryTextColor,
                        unfocusedTextColor = primaryTextColor
                    ),
                    textStyle = TextStyle(fontFamily = FunnelDisplay, color = primaryTextColor),
                    singleLine = true
                )

                errorMessage?.let {
                    Text(
                        text = it,
                        color = SalmonRed,
                        fontFamily = FunnelDisplay,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Button(
                    onClick = {
                        if (enteredKey == CLINICIAN_KEY) {
                            errorMessage = null
                            navController.navigate("clinicianDashboard")
                        } else {
                            errorMessage = "Invalid Clinician Key."
                            Toast.makeText(context, "Invalid Clinician Key", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        "Login as Clinician",
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
