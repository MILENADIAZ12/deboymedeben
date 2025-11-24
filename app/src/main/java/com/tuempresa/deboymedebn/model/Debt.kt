package com.tuempresa.deboymedebn.model


data class Debt(
    val id: String = "",
    val type: String = "",     // "debo" o "me deben"
    val nombre: String = "",
    val valor: Double = 0.0,
    val fechaLimite: String = ""
)
