package com.example.poulailler

data class Poule(
    val id: String = "",
    val nom: String = "", // Le nom de la poule
    val race: String = "", // La race de la poule
    val poids: String = "", // Le poids de la poule
    val caract: String? = "" // Une caractéristique (optionnelle)
   // val imageUrl: String? = null // URL ou chemin de l'image de la poule
)
