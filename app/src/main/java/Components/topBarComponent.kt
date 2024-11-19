
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarComponent(string: String) {
    TopAppBar(
        title = { Text(string) },
        actions = {
            IconButton(onClick = { /* Handle notifications */ }) {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications")
            }
        }
    )
}