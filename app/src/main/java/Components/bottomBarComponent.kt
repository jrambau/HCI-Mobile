import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Panel") },
            selected = currentRoute == "main",
            onClick = { navController.navigate("main") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.CreditCard, contentDescription = "Cards") },
            label = { Text("Tarjetas") },
            selected = currentRoute == "wallet",
            onClick = { navController.navigate("wallet") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.QrCode, contentDescription = "QR") },
            label = { Text("QR") },
            selected = currentRoute == "qr",
            onClick = { navController.navigate("qr") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Analytics, contentDescription = "Analytics") },
            label = { Text("Inversiones") },
            selected = currentRoute == "analytics",
            onClick = { navController.navigate("analytics") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Perfil") },
            selected = currentRoute == "profile",
            onClick = { navController.navigate("profile") }
        )
    }
}