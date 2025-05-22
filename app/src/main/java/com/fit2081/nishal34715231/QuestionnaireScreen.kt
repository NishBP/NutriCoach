package com.fit2081.nishal34715231

import android.app.TimePickerDialog
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
import androidx.navigation.NavHostController
import java.util.*

// Define colors as constants to match your design
// These colors match the ones in your requirements
val LimeGreen = Color(0xFFC1FF72)
val SalmonRed = Color(0xFFFF5757)

/**
 * Questionnaire Screen for food preferences and habits
 *
 * @param navController Navigation controller for screen navigation
 * @param userId The user ID passed from the login screen
 */
@Composable
fun QuestionnaireScreen(navController: NavHostController, userId: String) {
    // State variables to store user selections
    // For food categories
    val foodCategories = listOf("Fruits", "Vegetables", "Grains", "Red Meat", "Seafood", "Poultry", "Fish", "Eggs", "Nuts/Seeds")

    // Map to track which food categories are selected
    val selectedCategories = remember {
        mutableStateMapOf<String, Boolean>().apply {
            foodCategories.forEach { category ->
                put(category, false)
            }
        }
    }

    // For persona information
    val personas = listOf("Health Devotee", "Mindful Eater", "Wellness Striver", "Balance Seeker", "Health Procrastinator", "Food Carefree")

    // Map persona names to their image resources
    val personaImages = mapOf(
        "Health Devotee" to R.drawable.persona_1,
        "Mindful Eater" to R.drawable.persona_2,
        "Wellness Striver" to R.drawable.persona_3,
        "Balance Seeker" to R.drawable.persona_4,
        "Health Procrastinator" to R.drawable.persona_5,
        "Food Carefree" to R.drawable.persona_6
    )

    // Map persona names to their descriptions
    val personaDescriptions = mapOf(
        "Health Devotee" to "I’m passionate about healthy eating & health plays a big part in my life. I use social media to follow active lifestyle personalities or get new recipes/exercise ideas. I may even buy superfoods or follow a particular type of diet. I like to think I am super healthy.",
        "Mindful Eater" to "I’m health-conscious and being healthy and eating healthy is important to me. Although health means different things to different people, I make conscious lifestyle decisions about eating based on what I believe healthy means. I look for new recipes and healthy eating information on social media.",
        "Wellness Striver" to "I aspire to be healthy (but struggle sometimes). Healthy eating is hard work! I’ve tried to improve my diet, but always find things that make it difficult to stick with the changes. Sometimes I notice recipe ideas or healthy eating hacks, and if it seems easy enough, I’ll give it a go.",
        "Balance Seeker" to "I try and live a balanced lifestyle, and I think that all foods are okay in moderation. I shouldn’t have to feel guilty about eating a piece of cake now and again. I get all sorts of inspiration from social media like finding out about new restaurants, fun recipes and sometimes healthy eating tips.",
        "Health Procrastinator" to "I’m contemplating healthy eating but it’s not a priority for me right now. I know the basics about what it means to be healthy, but it doesn’t seem relevant to me right now. I have taken a few steps to be healthier but I am not motivated to make it a high priority because I have too many other things going on in my life.",
        "Food Carefree" to "\tI’m not bothered about healthy eating. I don’t really see the point and I don’t think about it. I don’t really notice healthy eating tips or recipes and I don’t care what I eat."
    )

    // State for selected persona
    var selectedPersona by remember { mutableStateOf("") }
    var showPersonaDropdown by remember { mutableStateOf(false) }

    // State for modal dialog
    var showPersonaDialog by remember { mutableStateOf(false) }
    var currentPersonaDetails by remember { mutableStateOf<Pair<String, Int>?>(null) }

    // State for time inputs
    var bigMealTime by remember { mutableStateOf("00:00") }
    var sleepTime by remember { mutableStateOf("Select time") }
    var wakeUpTime by remember { mutableStateOf("Select time") }

    // Context for TimePickerDialog
    val context = LocalContext.current

    // Main content
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // Make the screen scrollable
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Screen title
        Text(
            text = "Food Intake Questionaire",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontFamily = FunnelDisplay,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Food Categories Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = LimeGreen
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Tick all the food categories you can eat",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FunnelDisplay,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                HorizontalDivider(
                    color = Color.Black.copy(alpha = 0.2f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Food category checkboxes in a grid layout
                Column {
                    for (i in 0 until (foodCategories.size / 3 + 1)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            for (j in 0 until 3) {
                                val index = i * 3 + j
                                if (index < foodCategories.size) {
                                    val category = foodCategories[index]
                                    FoodCategoryButton(
                                        category = category,
                                        isSelected = selectedCategories[category] ?: false,
                                        onToggle = {
                                            selectedCategories[category] = !(selectedCategories[category] ?: false)
                                        }
                                    )
                                } else {
                                    // Empty space to maintain grid
                                    Spacer(modifier = Modifier.weight(1f))
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = SalmonRed
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Your Persona",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FunnelDisplay,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Divider(
                    color = Color.White.copy(alpha = 0.2f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Text(
                    text = "People can be broadly classified into 6 different types based on their eating preferences. Click on each button below to find out the different types, and select the type that best fits you!",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = FunnelDisplay
                    ),
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Persona buttons in a grid
                Column {
                    for (i in 0 until 2) { // 2 rows
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            for (j in 0 until 3) { // 3 columns
                                val index = i * 3 + j
                                if (index < personas.size) {
                                    val persona = personas[index]
                                    Button(
                                        onClick = {
                                            // Show persona details dialog
                                            currentPersonaDetails = Pair(persona, personaImages[persona] ?: R.drawable.persona_1)
                                            showPersonaDialog = true
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(4.dp)
                                            .height(48.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = LimeGreen,
                                            contentColor = Color.Black
                                        ),
                                        shape = RoundedCornerShape(24.dp)
                                    ) {
                                        Text(
                                            text = persona.split(" ")[0], // Just show first word to fit
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontFamily = FunnelDisplay,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Text(
                    text = "Which persona best fits you?",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FunnelDisplay,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )

                Divider(
                    color = Color.White.copy(alpha = 0.2f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Dropdown for persona selection
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Button(
                        onClick = { showPersonaDropdown = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LimeGreen,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(
                            text = if (selectedPersona.isEmpty()) "Select option" else selectedPersona,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontFamily = FunnelDisplay
                            )
                        )
                    }

                    DropdownMenu(
                        expanded = showPersonaDropdown,
                        onDismissRequest = { showPersonaDropdown = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(LimeGreen)
                    ) {
                        personas.forEach { persona ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = persona,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontFamily = FunnelDisplay
                                        )
                                    )
                                },
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = LimeGreen
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Timings",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FunnelDisplay,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Divider(
                    color = Color.Black.copy(alpha = 0.2f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Time picker rows
                TimePickerRow(
                    question = "What time of day approx. do you normally eat your biggest meal?",
                    time = bigMealTime,
                    onTimeSelected = { bigMealTime = it }
                )

                TimePickerRow(
                    question = "What time do you normally go to sleep?",
                    time = sleepTime,
                    onTimeSelected = { sleepTime = it }
                )

                TimePickerRow(
                    question = "What time do you normally wake up?",
                    time = wakeUpTime,
                    onTimeSelected = { wakeUpTime = it }
                )
            }
        }

        // Save Button
        Button(
            onClick = {
                // Save data to SharedPreferences
                saveQuestionnaireData(
                    context = context,
                    userId = userId,
                    selectedCategories = selectedCategories.filter { it.value }.keys.toList(),
                    selectedPersona = selectedPersona,
                    bigMealTime = bigMealTime,
                    sleepTime = sleepTime,
                    wakeUpTime = wakeUpTime
                )
                // Navigate to home screen with userId
                navController.navigate("home/$userId") {
                    // Clear the back stack so user can't go back to questionnaire using back button
                    popUpTo("welcome") { inclusive = false }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = LimeGreen
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Save",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FunnelDisplay,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }

    // Persona Details Dialog
    if (showPersonaDialog && currentPersonaDetails != null) {
        Dialog(onDismissRequest = { showPersonaDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Persona name
                    Text(
                        text = currentPersonaDetails!!.first,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontFamily = FunnelDisplay,
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Persona image
                    Image(
                        painter = painterResource(id = currentPersonaDetails!!.second),
                        contentDescription = "Persona image",
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .padding(bottom = 16.dp)
                    )

                    // Persona description
                    Text(
                        text = personaDescriptions[currentPersonaDetails!!.first] ?: "",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FunnelDisplay
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Close button
                    Button(
                        onClick = { showPersonaDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SalmonRed,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Close",
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
}

// A custom button for food category selection with a checkbox-like appearance

@Composable
fun FoodCategoryButton(
    category: String,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Button(
        onClick = onToggle,
        modifier = Modifier
            .padding(4.dp)
            .height(42.dp),
        colors = ButtonDefaults.buttonColors( // Sets the color of the checklist items
            containerColor = if (isSelected) SalmonRed else Color.White,
            contentColor = if (isSelected) Color.White else Color.Black
        ),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, SalmonRed)
    ) {
        Text(
            text = category,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FunnelDisplay
            )
        )
    }
}


// A row that contains a question and a time picker button

@Composable
fun TimePickerRow(
    question: String,
    time: String,
    onTimeSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Time picker dialog
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour: Int, minute: Int ->
            // Format time as HH:MM
            onTimeSelected(String.format("%02d:%02d", hour, minute))
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true // 24-hour format
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Question text
        Text(
            text = question,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FunnelDisplay
            ),
            modifier = Modifier.weight(3f)
        )

        // Time picker button
        Button(
            onClick = { timePickerDialog.show() },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = time,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FunnelDisplay
                )
            )
        }
    }
}


// Function to save questionnaire data to SharedPreferences

fun saveQuestionnaireData(
    context: android.content.Context,
    userId: String,
    selectedCategories: List<String>,
    selectedPersona: String,
    bigMealTime: String,
    sleepTime: String,
    wakeUpTime: String
) {
    // Get SharedPreferences instance
    val sharedPreferences = context.getSharedPreferences("NutriTrackerPrefs", android.content.Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()

    // Save data with userId prefix to keep data separate for each user
    val userPrefix = "user_${userId}_"

    // Save selected food categories as comma-separated string
    editor.putString("${userPrefix}food_categories", selectedCategories.joinToString(","))

    // Save persona
    editor.putString("${userPrefix}persona", selectedPersona)

    // Save time preferences
    editor.putString("${userPrefix}big_meal_time", bigMealTime)
    editor.putString("${userPrefix}sleep_time", sleepTime)
    editor.putString("${userPrefix}wake_up_time", wakeUpTime)

    // Commit changes
    editor.apply()
}