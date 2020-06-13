package ru.adonixis.aceventura56.model

import java.util.*

data class Pet(
    val imageUrl: String,
    val coordLatitude: Double,
    val coordLongitude: Double,
    val date: Date,
    val userEmail: String,
    val userName: String,
    val userId: String,
    val status: String,
    val petName: String,
    val petSex: String,
    val petAge: String,
    val petBreed: String
) {
    constructor() : this("", 0.0, 0.0, Date(), "", "", "", "", "", "", "", "")
}