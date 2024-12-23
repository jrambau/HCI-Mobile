package components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.width
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.lupay.R
import android.content.res.Configuration
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.layout.Spacer
import com.example.lupay.ui.utils.DeviceType
import com.example.lupay.ui.utils.rememberDeviceType

@Composable
fun TabletLayout(
    navigationRail: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Row(modifier = Modifier.fillMaxSize()) {
        navigationRail()
        androidx.compose.material3.Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp),
            color = Color(0xFFBDBDBD)
        )
        Box(modifier = Modifier.weight(1f)) {
            content()
        }
    }
}

@Composable
fun NavigationRail(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val deviceType = rememberDeviceType()
    val isTablet = deviceType == DeviceType.TABLET

    NavigationRail(
        modifier = Modifier
            .fillMaxHeight()
            .width(if (isTablet) 200.dp else if (isLandscape) 80.dp else 200.dp),
        containerColor = MaterialTheme.colorScheme.background
    ) {
        val items = listOf(
            Triple("main", stringResource(id = R.string.general), Icons.Default.Home),
            Triple("wallet", stringResource(id = R.string.cards), Icons.Default.CreditCard),
            Triple("qr", "QR", Icons.Default.QrCode),
            Triple("analytics", stringResource(id = R.string.investment), Icons.Default.Analytics),
            Triple("profile", stringResource(id = R.string.profile), Icons.Default.Person)
        )

        items.forEach { (route, title, icon) ->
            NavigationRailItem(
                selected = currentRoute == route,
                onClick = { onNavigate(route) },
                icon = {
                    if (isTablet) {
                        Row(
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .width(180.dp)
                                .padding(start = 12.dp)
                        ) {
                            Icon(
                                icon,
                                contentDescription = title,
                                tint = if (currentRoute == route) 
                                    MaterialTheme.colorScheme.primary 
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (currentRoute == route) 
                                    MaterialTheme.colorScheme.primary 
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Icon(
                            icon,
                            contentDescription = title,
                            tint = if (currentRoute == route) 
                                MaterialTheme.colorScheme.primary 
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                label = if (!isTablet && isLandscape) {
                    { Text(title) }
                } else {
                    null
                }
            )
        }
    }
} 