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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.lupay.R
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
                Triple("main", stringResource(id = R.string.general), Icons.Default.Home),
                Triple("wallet", stringResource(id = R.string.cards), Icons.Default.CreditCard),
                Triple("qr", "QR", Icons.Default.QrCode),
                Triple("analytics", stringResource(id = R.string.investment), Icons.Default.Analytics),
                Triple("profile", stringResource(id = R.string.profile), Icons.Default.Person)
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
                            text = title,
                            color = if (currentRoute == route) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall
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

