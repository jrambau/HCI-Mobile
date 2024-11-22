package components

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration
import com.example.lupay.R

@Composable
fun ReadOnlyInfoField(
    label: String,
    value: String
) {
    val configuration = LocalConfiguration.current
    val verticalPadding = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 0.dp else 2.dp
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = verticalPadding),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun PersonalInfoField(
    label: String,
    value: String,
    onEditClick: () -> Unit,
) {
    val configuration = LocalConfiguration.current
    val verticalPadding = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 0.dp else 2.dp
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = verticalPadding),
    ) {
        Column(
            modifier = Modifier
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        IconButton(
            onClick = onEditClick
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(id = R.string.edit) + " $label",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
} 