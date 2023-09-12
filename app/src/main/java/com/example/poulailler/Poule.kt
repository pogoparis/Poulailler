package com.example.poulailler

data class Poule(
    val id: String = "",
    val nom: String = "",
    val race: String = "",
    val poids: String = "",
    val caract: String? = "",
    val imageUrl: String? = null
)
