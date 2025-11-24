package com.tuempresa.deboymedebn.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    // DEVUELVE EL USUARIO ACTUAL COMPLETO
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    // Función para iniciar sesión
    fun login(
        email: String,
        password: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: ""
                onSuccess(uid)
            }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "Error desconocido")
            }
    }

    // Función para registrar un usuario
    fun register(
        email: String,
        password: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: ""
                onSuccess(uid)
            }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "Error desconocido")
            }
    }

    // Obtener el UID actual
    fun currentUserUid(): String? {
        return auth.currentUser?.uid
    }

    // Cerrar sesión
    fun logout() {
        auth.signOut()
    }
    fun currentUserEmail(): String {
        return auth.currentUser?.email ?: "Usuario"
    }



}
