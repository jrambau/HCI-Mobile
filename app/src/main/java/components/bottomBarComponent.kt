package components
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import theme.CustomTheme

@Composable
fun BottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val lightGreen = Color(0xFFE8F5E9)  // Light green color for selected items
    val lightSchemeBackground = Color(0xFFE0E0E0)  // Slightly darker gray for light scheme
    val darkSchemeBackground = Color(0xFF2C2C2C)   // Slightly lighter gray for dark scheme

    CustomTheme {
        val isDarkTheme = isSystemInDarkTheme()
        val backgroundColor = if (isDarkTheme) darkSchemeBackground else lightSchemeBackground

        NavigationBar(
            containerColor = backgroundColor,
            tonalElevation = 8.dp
        ) {
            val items = listOf(
                Triple("main", "Panel", Icons.Default.Home),
                Triple("wallet", "Tarjetas", Icons.Default.CreditCard),
                Triple("qr", "QR", Icons.Default.QrCode),
                Triple("analytics", "Inversiones", Icons.Default.Analytics),
                Triple("profile", "Perfil", Icons.Default.Person)
            )

            items.forEach { (route, title, icon) ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            icon,
                            contentDescription = title,
                            tint = if (currentRoute == route) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    label = {
                        Text(
                            title,
                            color = if (currentRoute == route) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    selected = currentRoute == route,
                    onClick = { navController.navigate(route) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = lightGreen,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}