// File: app/src/main/java/com/fit2081/nishal34715231/ui/theme/Type.kt
package com.fit2081.nishal34715231.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.fit2081.nishal34715231.R // Import R class to access font resources

// Define your custom FunnelDisplay FontFamily
val FunnelDisplay = FontFamily(
    Font(R.font.funnel_display_light, FontWeight.Light),
    Font(R.font.funnel_display_regular, FontWeight.Normal),
    Font(R.font.funnel_display_medium, FontWeight.Medium),
    Font(R.font.funnel_display_semibold, FontWeight.SemiBold),
    Font(R.font.funnel_display_bold, FontWeight.Bold),
    Font(R.font.funnel_display_extrabold, FontWeight.ExtraBold)
)

// Set of Material typography styles to start with
// You can customize these to use FunnelDisplay by default if desired,
// or apply FunnelDisplay selectively in your Composables.
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FunnelDisplay, // Example: Make bodyLarge use FunnelDisplay by default
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FunnelDisplay, // Example
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp, // Adjust as needed
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FunnelDisplay, // Example
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FunnelDisplay, // Example
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    )
    /* Other default text styles to override
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)
