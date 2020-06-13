package ru.adonixis.aceventura56.model

import java.util.*

data class PetRequest(
    val imageUrl: String,
    val coordLatitude: Double,
    val coordLongitude: Double,
    val date: Date,
    val userEmail: String,
    val userName: String,
    val userId: String,
    val status: String
)