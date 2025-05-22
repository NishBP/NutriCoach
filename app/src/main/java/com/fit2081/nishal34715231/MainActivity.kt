package com.fit2081.nishal34715231

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.fit2081.nishal34715231.ui.theme.Nishal34715231Theme
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Nishal34715231Theme {
                // Create a NavController to handle navigation within the app
                val navController = rememberNavController()

                // Define the navigation graph using NavHost
                NavHost(navController = navController, startDestination = "welcome") {
                    // Define the "welcome" route
                    composable("welcome") {
                        // Display the WelcomeScreen and pass a lambda to handle the Login button click
                        WelcomeScreen(onLoginButtonClicked = { navController.navigate("login") })
                    }

                    // Define the "login" route
                    composable("login") {
                        // Display the LoginScreen and pass the NavController for navigation
                        LoginScreen(navController = navController)
                    }

                    // Add the questionnaire route with userId parameter
                    composable(
                        route = "questionnaire/{userId}",
                        arguments = listOf(
                            navArgument("userId") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        // Extract the userId from the route
                        val userId = backStackEntry.arguments?.getString("userId") ?: ""
                        QuestionnaireScreen(
                            navController = navController,
                            userId = userId
                        )
                    }

                    // Add the home screen route with userId parameter
                    composable(
                        route = "home/{userId}",
                        arguments = listOf(
                            navArgument("userId") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        // Extract the userId from the route
                        val userId = backStackEntry.arguments?.getString("userId") ?: ""
                        HomeScreen(
                            navController = navController,
                            userId = userId
                        )
                    }

                    // Add insights route with userId parameter
                    composable(
                        route = "insights/{userId}",
                        arguments = listOf(
                            navArgument("userId") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        // Extract the userId from the route
                        val userId = backStackEntry.arguments?.getString("userId") ?: ""
                        InsightsScreen(
                            navController = navController,
                            userId = userId
                        )
                    }
                }
            }
        }
    }
}

val FunnelDisplay = FontFamily(
    Font(R.font.funnel_display_light, FontWeight.Light),
    Font(R.font.funnel_display_regular, FontWeight.Normal),
    Font(R.font.funnel_display_medium, FontWeight.Medium),
    Font(R.font.funnel_display_semibold, FontWeight.SemiBold),
    Font(R.font.funnel_display_bold, FontWeight.Bold),
    Font(R.font.funnel_display_extrabold, FontWeight.ExtraBold)
)

@Composable
fun WelcomeScreen(onLoginButtonClicked: () -> Unit) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val monashNutritionUrl = "https://www.monash.edu/medicine/scs/nutrition/clinics/nutrition"

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top section - logo and name
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 80.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = stringResource(id = R.string.app_logo),
                    modifier = Modifier.size(100.dp)
                )

                Text(
                    text = "NutriTracker",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontFamily = FunnelDisplay,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    textAlign = TextAlign.Center
                )
            }

            // Middle section - disclaimer and link
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 24.dp)
            ) {
                Text(
                    text = "This app provides general health and nutrition information for educational purposes only. It is not intended as medical advice, diagnosis, or treatment. Always consult a qualified healthcare professional before making any changes to your diet, exercise, or health regimen. Use this app at your own risk.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FunnelDisplay,
                        fontWeight = FontWeight.Light,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.secondary
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                ClickableText(
                    text = AnnotatedString("Learn more at Monash Nutrition Clinic"),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FunnelDisplay,
                        fontWeight = FontWeight.Light,
                        color = Color(0xFFff5757),
                        textAlign = TextAlign.Center
                    ),
                    onClick = {
                        uriHandler.openUri(monashNutritionUrl)
                    }
                )
            }

            // Button section
            Button(
                onClick = onLoginButtonClicked,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF000000),
                    contentColor = Color(0xFFc1ff72)
                )
            ) {
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = FunnelDisplay,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            // Bottom section - name and ID
            Text(
                text = "Nishal Paranagama (34715231)",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FunnelDisplay,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}