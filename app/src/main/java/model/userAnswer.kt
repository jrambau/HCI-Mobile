package com.example.lupay.ui.model
import kotlinx.serialization.Serializable

@Serializable
data class UserAnswer(
    val answer: String,
    val questionId: Int
)


