package theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

// Use these colors throughout your app
// Primary colors
// Green: Main action buttons, primary elements
// DarkGreen: Pressed state of Green buttons
// Gray shades: Backgrounds, text, and secondary elements
// Text colors: For various text elements
// CardBorder: For card and input field borders

// Main action color (e.g., primary buttons)
private val Green = Color(0xFF4CAF50)
// Pressed state of Green buttons
private val DarkGreen = Color(0xFF3B8A3E)
// Main background color
private val LightGray = Color(0xFFF5F5F5)
// Secondary background color (e.g., card backgrounds)
private val MediumGray = Color(0xFFEEEEEE)
// Used for secondary text and icons
private val DarkGray = Color(0xFF666666)
// Main text color
private val TextPrimary = Color(0xFF1A1A1A)
// Secondary text color
private val TextSecondary = Color(0xFF757575)
// Border color for cards and input fields
private val CardBorder = Color(0xFFE0E0E0)

private val DarkColorScheme = darkColorScheme(
    primary = Green,
    primaryContainer = DarkGreen,
    secondary = DarkGray,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2D2D2D),
    outlineVariant = Color(0xFF404040)
)

private val LightColorScheme = lightColorScheme(
    primary = Green,
    primaryContainer = Green,
    secondary = DarkGray,
    background = LightGray,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    surfaceVariant = MediumGray,
    outlineVariant = CardBorder
)

// Custom shapes based on the app design
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),  // For small elements like chips
    small = RoundedCornerShape(8.dp),       // For buttons, input fields
    medium = RoundedCornerShape(16.dp),     // For cards, dialogs
    large = RoundedCornerShape(24.dp),      // For bottom sheets, larger cards
    extraLarge = RoundedCornerShape(32.dp)  // For full-screen dialogs
)

// Custom typography based on the app design
val Typography = Typography(
    headlineLarge = TextStyle(
        // Use for main screen titles
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        // Use for section headers
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        // Use for card titles, dialog titles
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        // Use for smaller section titles, important text
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    bodyLarge = TextStyle(
        // Use for main body text
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        // Use for secondary text, descriptions
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    labelLarge = TextStyle(
        // Use for button text, input labels
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )
)

@Composable
fun CustomTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Set dynamicColor to true if you want to use Android 12+ dynamic color
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Optional: Update status bar color
    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (LocalContext.current as Activity).window
        window.statusBarColor = colorScheme.primary.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}