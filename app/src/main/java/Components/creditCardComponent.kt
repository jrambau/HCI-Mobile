import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lupay.R

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
    val cardType = getCardType(cardNumber)

    Card(
        modifier = modifier
            .width(350.dp)
            .height(200.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(20.dp)
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
                    Icon(
                        painter = painterResource(
                            id = when (cardType) {
                                CardType.VISA -> R.drawable.visa
                                CardType.MASTERCARD -> R.drawable.ma_symbol_opt_45_1x
                                CardType.AMEX -> R.drawable.amx
                                CardType.UNKNOWN -> R.drawable.ic_launcher_foreground
                            }
                        ),
                        contentDescription = "Card Logo",
                        modifier = Modifier.height(40.dp)
                    )
                }

                // Card Number
                Text(
                    text = formatCardNumber(cardNumber, isHidden),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = Color(0xFF2C3E50)
                )

                // Card Name
                Text(
                    text = cardName.uppercase(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C3E50)
                )

                // Card Details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "VENCIMIENTO",
                            fontSize = 10.sp,
                            color = Color(0xFF7F8C8D)
                        )
                        Text(
                            text = formatExpiry(cardExpiry, isHidden),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C3E50)
                        )
                    }
                    Column {
                        Text(
                            text = "CVV",
                            fontSize = 10.sp,
                            color = Color(0xFF7F8C8D)
                        )
                        Text(
                            text = formatCVV(cvv, isHidden),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C3E50)
                        )
                    }
                }
            }
        }
    }
}

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