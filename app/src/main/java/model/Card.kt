package model

data class Card(
    val id: Int,
    val cardNumber: String,
    val expirationDate: String,
    val cvv: String,
    val cardHolder: String
)