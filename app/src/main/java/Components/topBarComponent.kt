import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import theme.CustomTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarComponent(name: String, title: String, navController: NavController) {
    val initials = name.split(" ").take(2).joinToString(" ") { it.take(1).uppercase() }

    // Applying CustomTheme for consistent styling
    CustomTheme {
        TopAppBar(
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium // Use headline style from the theme
                )
            },
            actions = {
                // Circle with initials on the top right
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary) // Use primary color from the theme
                        .padding(8.dp)
                        .clickable { navController.navigate("profile") }, // Navigate to perfil when clicked
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 16.sp
                        ) // White color for the initials
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background, // Use background color from the theme
                titleContentColor = MaterialTheme.colorScheme.onBackground // Title color based on theme
            )
        )
    }
}

