package components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import theme.CustomTheme
import com.example.lupay.R
import android.content.res.Configuration
import androidx.compose.ui.platform.LocalConfiguration

enum class CardType {
    VISA, MASTERCARD, AMEX, UNKNOWN
}

@Composable
fun CreditCard(
    cardNumber: String,
    cardName: String,
    cardExpiry: String,
    isHidden: Boolean,
    modifier: Modifier = Modifier
) {
    CustomTheme {
        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        val cardWidth = if (isLandscape) 280.dp else 320.dp
        val cardHeight = if (isLandscape) 150.dp else 200.dp
        
        val cardType = getCardType(cardNumber)
        val isDarkTheme = isSystemInDarkTheme()

        val cardBackgroundColor = if (isDarkTheme) Color.Black else Color.White
        val cardBorderColor = if (isDarkTheme) Color.White else Color.Black
        val textColor = if (isDarkTheme) Color.White else Color.Black
        val secondaryTextColor = if (isDarkTheme) Color.LightGray else Color(0xFF444444)
        val tertiaryTextColor = if (isDarkTheme) Color.Gray else Color(0xFF999999)

        Card(
            modifier = modifier
                .width(cardWidth)
                .height(cardHeight)
                .border(
                    width = 1.dp,
                    color = cardBorderColor,
                    shape = RoundedCornerShape(16.dp)
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(cardBackgroundColor)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Card Logo
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        if (cardType != CardType.UNKNOWN) {
                            Icon(
                                painter = painterResource(
                                    id = when (cardType) {
                                        CardType.VISA -> R.drawable.visa
                                        CardType.MASTERCARD -> R.drawable.ma_symbol_opt_45_1x
                                        CardType.AMEX -> R.drawable.amx
                                        else -> 0 // Should never reach here
                                    }
                                ),
                                contentDescription = "Card Logo",
                                modifier = Modifier
                                    .size(
                                        when (cardType) {
                                            CardType.VISA -> 40.dp
                                            CardType.MASTERCARD, CardType.AMEX -> 45.dp
                                            else -> 0.dp
                                        }
                                    )
                                    .padding(end = 4.dp),
                                tint = Color.Unspecified
                            )
                        }
                    }

                    // Card Number
                    Text(
                        text = formatCardNumber(cardNumber, isHidden),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = textColor,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    // Card Name
                    Text(
                        text = cardName.uppercase(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = secondaryTextColor,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    // Card Expiry (VENCIMIENTO)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Expiry Column (VENCIMIENTO)
                        Column(
                            modifier = Modifier.padding(end = 16.dp),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = stringResource(id = R.string.expiration_cap),
                                fontSize = 10.sp,
                                color = tertiaryTextColor,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                            Text(
                                text = formatExpiry(cardExpiry, isHidden),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor,
                            )
                        }
                    }
                }
            }
        }
    }
}


// Updated helper functions
fun getCardType(cardNumber: String): CardType {
    return when {
        cardNumber.startsWith("4") -> CardType.VISA
        cardNumber.matches(Regex("^(51|52|53|54|55).*")) -> CardType.MASTERCARD
        cardNumber.matches(Regex("^(34|37).*")) -> CardType.AMEX
        else -> CardType.UNKNOWN
    }
}

fun formatCardNumber(number: String, isHidden: Boolean): String {
    val limitedNumber = number.take(16)
    return if (isHidden) {
        "•••• •••• •••• ••••"
    } else {
        limitedNumber.chunked(4).joinToString(" ")
    }
}

fun formatExpiry(expiry: String, isHidden: Boolean): String {
    return if (isHidden) {
        "••/••"
    } else {
        val cleanExpiry = expiry.replace(Regex("[^0-9]"), "")
        if (cleanExpiry.length >= 4) {
            "${cleanExpiry.substring(0, 2)}/${cleanExpiry.substring(2, 4)}"
        } else {
            expiry
        }
    }
}


