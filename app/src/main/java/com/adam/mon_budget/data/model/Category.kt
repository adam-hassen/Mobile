package com.adam.mon_budget.data.model

enum class Category(val libelle: String, val iconName: String) {
    TRANSPORT("Transport", "directions_car"),
    LOISIRS("Loisirs", "sports_esports"),
    SHOPPING("Shopping", "shopping_cart"),
    LOGEMENT("Logement", "home"),
    NOURRITURE("Nourriture", "restaurant"),
    AUTRE("Autre", "more_horiz")
}
