// File: app/src/main/java/com/fit2081/nishal34715231/MainActivity.kt
package com.fit2081.nishal34715231

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fit2081.nishal34715231.ui.theme.Nishal34715231Theme
import com.fit2081.nishal34715231.viewmodel.FoodIntakeViewModel
import com.fit2081.nishal34715231.viewmodel.PatientViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

val FunnelDisplay = FontFamily(
    Font(R.font.funnel_display_light, FontWeight.Light),
    Font(R.font.funnel_display_regular, FontWeight.Normal),
    Font(R.font.funnel_display_medium, FontWeight.Medium),
    Font(R.font.funnel_display_semibold, FontWeight.SemiBold),
    Font(R.font.funnel_display_bold, FontWeight.Bold),
    Font(R.font.funnel_display_extrabold, FontWeight.ExtraBold)
)

// Session states to avoid magic strings and improve type safety
sealed class SessionState {
    object Loading : SessionState()
    object NoSession : SessionState()
    data class ValidSession(val userId: String, val destination: String) : SessionState()
}

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val PREFS_NAME = "NutriCoachPrefs"
        private const val KEY_LOGGED_IN_USER_ID = "LOGGED_IN_USER_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Nishal34715231Theme {
                SessionManager()
            }
        }
    }
}

@Composable
fun SessionManager() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val patientViewModel: PatientViewModel = viewModel()
    val foodIntakeViewModel: FoodIntakeViewModel = viewModel()

    // Single state to manage session status
    var sessionState by remember { mutableStateOf<SessionState>(SessionState.Loading) }

    // Check session only once when the app starts
    LaunchedEffect(Unit) {
        Log.d("MainActivity", "Starting session check...")
        sessionState = checkUserSession(context, patientViewModel, foodIntakeViewModel)
        Log.d("MainActivity", "Session check completed: $sessionState")
    }

    // Handle navigation based on session state
    when (sessionState) {
        is SessionState.Loading -> {
            // Show loading screen while checking session
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is SessionState.NoSession -> {
            // No valid session, show login
            AppNavigation(
                navController = navController,
                startDestination = "login"
            )
        }

        is SessionState.ValidSession -> {
            // Valid session found, navigate to appropriate screen
            AppNavigation(
                navController = navController,
                startDestination = (sessionState as SessionState.ValidSession).destination
            )
        }
    }
}

@Composable
fun AppNavigation(
    navController: androidx.navigation.NavHostController,
    startDestination: String
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable(
            route = "questionnaire/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            QuestionnaireScreen(navController = navController, userId = userId)
        }
        composable(
            route = "home/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            HomeScreen(navController = navController, userId = userId)
        }
        composable(
            route = "insights/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            InsightsScreen(navController = navController, userId = userId)
        }
        composable(
            route = "settings/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            SettingsScreen(navController = navController, userId = userId)
        }
        composable("clinicianLogin") {
            ClinicianLoginScreen(navController = navController)
        }
        composable("clinicianDashboard") {
            ClinicianDashboardScreen(navController = navController)
        }
        composable(
            "nutricoach/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            NutriCoachScreen(navController = navController, userId = userId)
        }
    }
}

// Separate suspend function to handle session checking logic
private suspend fun checkUserSession(
    context: Context,
    patientViewModel: PatientViewModel,
    foodIntakeViewModel: FoodIntakeViewModel
): SessionState = withContext(Dispatchers.IO) {
    try {
        val prefs = context.getSharedPreferences("NutriCoachPrefs", Context.MODE_PRIVATE)
        val loggedInUserId = prefs.getString("LOGGED_IN_USER_ID", null)

        if (loggedInUserId.isNullOrEmpty()) {
            Log.d("MainActivity", "No stored user ID found")
            return@withContext SessionState.NoSession
        }

        Log.d("MainActivity", "Found stored user ID: $loggedInUserId")

        // Verify the user still exists and account is claimed
        val patient = patientViewModel.getPatientByIdOnce(loggedInUserId)
        if (patient == null || !patient.isAccountClaimed) {
            Log.w("MainActivity", "Invalid session - patient not found or account not claimed")
            // Clear invalid session
            prefs.edit().remove("LOGGED_IN_USER_ID").apply()
            return@withContext SessionState.NoSession
        }

        Log.d("MainActivity", "Valid patient found: ${patient.name}")

        // Check if user has completed questionnaire
        val foodIntakeRecord = foodIntakeViewModel.getFoodIntakeDataOnce(loggedInUserId)
        val destination = if (foodIntakeRecord != null) {
            "home/$loggedInUserId"
        } else {
            "questionnaire/$loggedInUserId"
        }

        Log.d("MainActivity", "Determined destination: $destination")
        return@withContext SessionState.ValidSession(loggedInUserId, destination)

    } catch (e: Exception) {
        Log.e("MainActivity", "Error during session check", e)
        // Clear potentially corrupted session data
        val prefs = context.getSharedPreferences("NutriCoachPrefs", Context.MODE_PRIVATE)
        prefs.edit().remove("LOGGED_IN_USER_ID").apply()
        return@withContext SessionState.NoSession
    }
}