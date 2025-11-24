package com.tuempresa.deboymedebn.model


data class Contact(
    var id: String = "",
    val name: String = "",
    val phone: String = "",
    val description: String = "",
    val amount: Double = 0.0,     // SOLO numérico
    val dueDate: String = "",
    val type: String = "meDeben", // "meDeben" o "debo"
    val isPaid: Boolean = false
)
