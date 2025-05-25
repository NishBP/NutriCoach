// File: app/src/main/java/com/fit2081/nishal34715231/QuestionnaireScreen.kt
package com.fit2081.nishal34715231

import android.app.TimePickerDialog
import android.content.Context
import android.widget.Toast // Import for showing Toast messages
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.fit2081.nishal34715231.data.FoodIntakeData
import com.fit2081.nishal34715231.viewmodel.FoodIntakeViewModel
import java.util.*

val SalmonRed = Color(0xFFFF5757)

@Composable
fun QuestionnaireScreen(navController: NavHostController, userId: String) {
    val context = LocalContext.current
    val foodIntakeViewModel: FoodIntakeViewModel = viewModel()

    // State for form fields
    val foodCategories = listOf("Fruits", "Vegetables", "Grains", "Red Meat", "Seafood", "Poultry", "Fish", "Eggs", "Nuts/Seeds")
    val selectedCategoriesState = remember { mutableStateMapOf<String, Boolean>() }

    val personas = listOf("Health Devotee", "Mindful Eater", "Wellness Striver", "Balance Seeker", "Health Procrastinator", "Food Carefree")
    var selectedPersona by remember { mutableStateOf("") }
    var showPersonaDropdown by remember { mutableStateOf(false) }

    var showPersonaDialog by remember { mutableStateOf(false) }
    var currentPersonaDetails by remember { mutableStateOf<Pair<String, Int>?>(null) }

    // Default time values - these will be updated if data is loaded from DB
    var bigMealTime by remember { mutableStateOf("Select time") }
    var sleepTime by remember { mutableStateOf("Select time") }
    var wakeUpTime by remember { mutableStateOf("Select time") }

    // State for validation error messages
    var validationErrorMessage by remember { mutableStateOf<String?>(null) }

    // --- ViewModel Data Loading and State Update for Prefilling ---
    val foodIntakeDataFromDb by foodIntakeViewModel.currentUserFoodIntake.observeAsState()

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            foodIntakeViewModel.loadFoodIntakeData(userId)
        }
    }

    LaunchedEffect(foodIntakeDataFromDb) {
        foodIntakeDataFromDb?.let { data ->
            selectedPersona = data.persona ?: ""

            // Map DB times to UI states. If DB time is null/empty, keep "Select time"
            bigMealTime = data.breakfastTime?.takeIf { it.isNotEmpty() } ?: "Select time"
            sleepTime = data.lunchTime?.takeIf { it.isNotEmpty() } ?: "Select time"
            wakeUpTime = data.dinnerTime?.takeIf { it.isNotEmpty() } ?: "Select time"

            selectedCategoriesState.keys.forEach { key -> selectedCategoriesState[key] = false }
            data.foodCategories?.forEach { category ->
                if (foodCategories.contains(category)) {
                    selectedCategoriesState[category] = true
                }
            }
            // Clear any previous validation error when data is prefilled
            validationErrorMessage = null
        }
    }
    // --- End of ViewModel Data Loading ---

    // Persona images and descriptions (as in your original code)
    val personaImages = mapOf(
        "Health Devotee" to R.drawable.persona_1, "Mindful Eater" to R.drawable.persona_2,
        "Wellness Striver" to R.drawable.persona_3, "Balance Seeker" to R.drawable.persona_4,
        "Health Procrastinator" to R.drawable.persona_5, "Food Carefree" to R.drawable.persona_6
    )
    val personaDescriptions = mapOf(
        "Health Devotee" to "I’m passionate about healthy eating & health plays a big part in my life...",
        // ... (include all your persona descriptions)
        "Mindful Eater" to "I’m health-conscious and being healthy and eating healthy is important to me...",
        "Wellness Striver" to "I aspire to be healthy (but struggle sometimes). Healthy eating is hard work!...",
        "Balance Seeker" to "I try and live a balanced lifestyle, and I think that all foods are okay in moderation...",
        "Health Procrastinator" to "I’m contemplating healthy eating but it’s not a priority for me right now...",
        "Food Carefree" to "I’m not bothered about healthy eating. I don’t really see the point and I don’t think about it..."
    )


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Food Intake Questionnaire",
            style = MaterialTheme.typography.headlineLarge.copy(fontFamily = FunnelDisplay, fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Validation Error Message Display
        validationErrorMessage?.let {
            Text(
                text = it,
                color = SalmonRed,
                style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FunnelDisplay),
                modifier = Modifier.padding(bottom = 12.dp),
                textAlign = TextAlign.Center
            )
        }

        // Food Categories Section
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFC1FF72)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Tick all the food categories you can eat",
                    style = MaterialTheme.typography.titleLarge.copy(fontFamily = FunnelDisplay, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                HorizontalDivider(color = Color.Black.copy(alpha = 0.2f), thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                Column {
                    // Using selectedCategoriesState for FoodCategoryButton
                    (0 until (foodCategories.size + 2) / 3).forEach { rowIndex ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            (0 until 3).forEach { colIndex ->
                                val index = rowIndex * 3 + colIndex
                                if (index < foodCategories.size) {
                                    val category = foodCategories[index]
                                    FoodCategoryButton(
                                        category = category,
                                        isSelected = selectedCategoriesState[category] ?: false,
                                        onToggle = { selectedCategoriesState[category] = !(selectedCategoriesState[category] ?: false) }
                                    )
                                } else {
                                    Spacer(modifier = Modifier.weight(1f)) // Fill empty space in grid
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        // Persona Section
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = SalmonRed),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Your Persona",
                    style = MaterialTheme.typography.titleLarge.copy(fontFamily = FunnelDisplay, fontWeight = FontWeight.Bold),
                    color = Color.White, modifier = Modifier.padding(bottom = 8.dp)
                )
                Divider(color = Color.White.copy(alpha = 0.2f), thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    text = "People can be broadly classified into 6 different types... Click on each button... select the type that best fits you!",
                    style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FunnelDisplay),
                    color = Color.White, modifier = Modifier.padding(bottom = 16.dp)
                )
                // Persona buttons grid
                Column {
                    (0 until 2).forEach { rowIndex ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            (0 until 3).forEach { colIndex ->
                                val index = rowIndex * 3 + colIndex
                                if (index < personas.size) {
                                    val persona = personas[index]
                                    Button(
                                        onClick = {
                                            currentPersonaDetails = Pair(persona, personaImages[persona] ?: R.drawable.persona_1)
                                            showPersonaDialog = true
                                        },
                                        modifier = Modifier.weight(1f).padding(4.dp).height(48.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC1FF72), contentColor = Color.Black),
                                        shape = RoundedCornerShape(24.dp)
                                    ) {
                                        Text(persona.split(" ")[0], style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FunnelDisplay, fontWeight = FontWeight.Bold))
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                Text(
                    text = "Which persona best fits you?",
                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = FunnelDisplay, fontWeight = FontWeight.Bold),
                    color = Color.White, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
                Divider(color = Color.White.copy(alpha = 0.2f), thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Button(
                        onClick = { showPersonaDropdown = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC1FF72), contentColor = Color.Black),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(if (selectedPersona.isEmpty()) "Select option" else selectedPersona, style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FunnelDisplay))
                    }
                    DropdownMenu(
                        expanded = showPersonaDropdown,
                        onDismissRequest = { showPersonaDropdown = false },
                        modifier = Modifier.fillMaxWidth().background(Color(0xFFC1FF72))
                    ) {
                        personas.forEach { persona ->
                            DropdownMenuItem(
                                text = { Text(persona, style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FunnelDisplay)) },
                                onClick = {
                                    selectedPersona = persona
                                    showPersonaDropdown = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Timings Section
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFC1FF72)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Timings",
                    style = MaterialTheme.typography.titleLarge.copy(fontFamily = FunnelDisplay, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Divider(color = Color.Black.copy(alpha = 0.2f), thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                TimePickerRow(question = "What time of day approx. do you normally eat your biggest meal?", time = bigMealTime, onTimeSelected = { bigMealTime = it })
                TimePickerRow(question = "What time do you normally go to sleep?", time = sleepTime, onTimeSelected = { sleepTime = it })
                TimePickerRow(question = "What time do you normally wake up?", time = wakeUpTime, onTimeSelected = { wakeUpTime = it })
            }
        }

        // Save Button
        Button(
            onClick = {
                validationErrorMessage = null // Clear previous error
                val currentlySelectedCategories = selectedCategoriesState.filter { it.value }.keys.toList()

                // Validation Logic
                if (currentlySelectedCategories.isEmpty()) {
                    validationErrorMessage = "Please select at least one food category."
                    return@Button
                }
                if (selectedPersona.isEmpty()) {
                    validationErrorMessage = "Please select your persona."
                    return@Button
                }
                if (bigMealTime == "Select time" || sleepTime == "Select time" || wakeUpTime == "Select time") {
                    validationErrorMessage = "Please select all three meal/activity times."
                    return@Button
                }
                if (bigMealTime == sleepTime || bigMealTime == wakeUpTime || sleepTime == wakeUpTime) {
                    validationErrorMessage = "Meal/activity times must be different from each other."
                    return@Button
                }

                // If validation passes:
                val foodIntakeToSave = FoodIntakeData(
                    // If editing, use existing ID, otherwise 0 for Room to autoGenerate
                    id = foodIntakeDataFromDb?.id ?: 0,
                    patientUserId = userId,
                    foodCategories = currentlySelectedCategories,
                    persona = selectedPersona,
                    // Map UI times to DB fields
                    breakfastTime = bigMealTime, // "Biggest meal time"
                    lunchTime = sleepTime,     // "Sleep time"
                    dinnerTime = wakeUpTime      // "Wake up time"
                )

                foodIntakeViewModel.saveFoodIntakeData(foodIntakeToSave)
                Toast.makeText(context, "Questionnaire saved!", Toast.LENGTH_SHORT).show()

                // Persist that questionnaire is done for this user (for LoginScreen logic)
                val prefs = context.getSharedPreferences("NutriCoachPrefs", Context.MODE_PRIVATE)
                prefs.edit().putBoolean("user_${userId}_questionnaire_completed_db", true).apply()


                navController.navigate("home/$userId") {
                    popUpTo("welcome") { inclusive = false }
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp).padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color(0xFFC1FF72)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Save", style = MaterialTheme.typography.titleLarge.copy(fontFamily = FunnelDisplay, fontWeight = FontWeight.Bold))
        }
    }

    // Persona Details Dialog
    if (showPersonaDialog && currentPersonaDetails != null) {
        Dialog(onDismissRequest = { showPersonaDialog = false }) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = currentPersonaDetails!!.first,
                        style = MaterialTheme.typography.headlineSmall.copy(fontFamily = FunnelDisplay, fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Image(
                        painter = painterResource(id = currentPersonaDetails!!.second),
                        contentDescription = "Persona image",
                        modifier = Modifier.size(200.dp).clip(RoundedCornerShape(8.dp)).padding(bottom = 16.dp)
                    )
                    Text(
                        text = personaDescriptions[currentPersonaDetails!!.first] ?: "",
                        style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FunnelDisplay),
                        textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Button(
                        onClick = { showPersonaDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = SalmonRed, contentColor = Color.White)
                    ) {
                        Text("Close", style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FunnelDisplay, fontWeight = FontWeight.Bold))
                    }
                }
            }
        }
    }
}

// FoodCategoryButton composable
@Composable
fun FoodCategoryButton(category: String, isSelected: Boolean, onToggle: () -> Unit) {
    Button(
        onClick = onToggle,
        modifier = Modifier.padding(4.dp).height(42.dp).defaultMinSize(minWidth = 80.dp), // Ensure some min width
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) SalmonRed else Color.White,
            contentColor = if (isSelected) Color.White else Color.Black
        ),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, SalmonRed)
    ) {
        Text(text = category, style = MaterialTheme.typography.bodySmall.copy(fontFamily = FunnelDisplay), textAlign = TextAlign.Center)
    }
}

// TimePickerRow composable
@Composable
fun TimePickerRow(question: String, time: String, onTimeSelected: (String) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val initialHour = if (time != "Select time" && time.matches(Regex("\\d{2}:\\d{2}"))) {
        time.split(":")[0].toInt()
    } else {
        calendar.get(Calendar.HOUR_OF_DAY)
    }
    val initialMinute = if (time != "Select time" && time.matches(Regex("\\d{2}:\\d{2}"))) {
        time.split(":")[1].toInt()
    } else {
        calendar.get(Calendar.MINUTE)
    }

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour: Int, minute: Int -> onTimeSelected(String.format("%02d:%02d", hour, minute)) },
        initialHour, initialMinute, true // 24-hour format
    )

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = question, style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FunnelDisplay), modifier = Modifier.weight(3f))
        Button(
            onClick = { timePickerDialog.show() },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = time, style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FunnelDisplay))
        }
    }
}
