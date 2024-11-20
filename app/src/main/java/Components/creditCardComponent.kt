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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lupay.R
import theme.CustomTheme

enum class CardType {
    VISA, MASTERCARD, AMEX, UNKNOWN
}

@Composable
fun CreditCard(
    cardNumber: String,
    cardName: String,
    cardExpiry: String,
    cvv: String,
    isHidden: Boolean,
    modifier: Modifier = Modifier
) {
    CustomTheme {
        val cardType = getCardType(cardNumber)
        val isDarkTheme = isSystemInDarkTheme()

        val cardBackgroundColor = if (isDarkTheme) Color.Black else Color.White
        val cardBorderColor = if (isDarkTheme) Color.White else Color.Black
        val textColor = if (isDarkTheme) Color.White else Color.Black
        val secondaryTextColor = if (isDarkTheme) Color.LightGray else Color(0xFF444444)
        val tertiaryTextColor = if (isDarkTheme) Color.Gray else Color(0xFF999999)

        Card(
            modifier = modifier
                .width(320.dp)
                .height(200.dp)
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
                    .height(400.dp)
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

                    // Card Details (VENCIMIENTO and CVV)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Expiry Column (VENCIMIENTO)
                        Column(
                            modifier = Modifier.padding(end = 22.dp),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "VENCIMIENTO",
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

                        // CVV Column
                        Column(
                            modifier = Modifier.padding(start = 16.dp),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "CVV",
                                fontSize = 10.sp,
                                color = tertiaryTextColor,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                            Text(
                                text = formatCVV(cvv, isHidden),
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

// The helper functions remain unchanged
fun getCardType(cardNumber: String): CardType {
    return when {
        cardNumber.startsWith("4") -> CardType.VISA
        cardNumber.matches(Regex("^(51|52|53|54|55).*")) -> CardType.MASTERCARD
        cardNumber.matches(Regex("^(34|37).*")) -> CardType.AMEX
        else -> CardType.UNKNOWN
    }
}

fun formatCardNumber(number: String, isHidden: Boolean): String {
    return if (isHidden) {
        "•••• •••• •••• ••••"
    } else {
        number.chunked(4).joinToString(" ")
    }
}

fun formatExpiry(expiry: String, isHidden: Boolean): String {
    return if (isHidden) "••/••" else expiry
}

fun formatCVV(cvv: String, isHidden: Boolean): String {
    return if (isHidden) "•••" else cvv
}