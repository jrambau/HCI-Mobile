package theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4CAF50),  // Botones verdes
    secondary = Color(0xFF666666),
    background = Color(0xFF121212), // Fondo oscuro
    surface = Color(0xFF1E1E1E),    // Contenedores oscuros
    onPrimary = Color.White,        // Texto blanco en botones
    onSecondary = Color.White,      // Texto claro en secundarios
    onBackground = Color.White,     // Texto claro
    onSurface = Color.White         // Texto claro en contenedores
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4CAF50),    // Botones verdes
    secondary = Color(0xFFAAAAAA),
    background = Color(0xFFF5F5F5), // Fondo gris claro
    surface = Color(0xFFEEEEEE),    // Contenedores grises claros
    onPrimary = Color.White,        // Texto blanco en botones
    onSecondary = Color.Black,      // Texto oscuro en secundarios
    onBackground = Color.Black,     // Texto oscuro
    onSurface = Color.Black         // Texto oscuro en contenedores
)

@Composable
fun CustomTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
