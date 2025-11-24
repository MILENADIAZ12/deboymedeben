package com.tuempresa.deboymedebn.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tuempresa.deboymedebn.model.Contact
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ContactsViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> get() = _contacts

    private var listenerRegistered = false

    fun loadContacts() {
        val uid = auth.currentUser?.uid

        if (uid == null) {
            Log.e("Firestore", "Usuario no autenticado.")
            _contacts.value = emptyList()
            return
        }

        if (listenerRegistered) return

        firestore.collection("users")
            .document(uid)
            .collection("contacts")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("Firestore", "Error snapshot:", error)
                    return@addSnapshotListener
                }

                val list = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Contact(
                            id = doc.getString("id") ?: doc.id,   // ← IMPORTANTE
                            name = doc.getString("name") ?: "",
                            phone = doc.getString("phone") ?: "",
                            description = doc.getString("description") ?: "",
                            amount = extractDouble(doc.get("amount")) ?: 0.0,
                            dueDate = doc.getString("dueDate") ?: "",
                            type = doc.getString("type") ?: "meDeben",
                            isPaid = doc.getBoolean("isPaid") ?: false
                        )
                    } catch (e: Exception) {
                        Log.e("Firestore", "Error al convertir documento: ${doc.id}", e)
                        null
                    }
                } ?: emptyList()

                _contacts.value = list
            }

        listenerRegistered = true
    }

    private fun extractDouble(value: Any?): Double? {
        return when (value) {
            is Number -> value.toDouble()
            is String -> value.toDoubleOrNull()
            else -> null
        }
    }

    fun addContact(contact: Contact) {
        val uid = auth.currentUser?.uid ?: return

        val userContactsRef = firestore.collection("users")
            .document(uid)
            .collection("contacts")

        userContactsRef
            .add(contact)
            .addOnSuccessListener { doc ->
                val generatedId = doc.id

                // Guardar el ID dentro del documento
                doc.update("id", generatedId)

                Log.d("Firestore", "Contacto guardado con ID: $generatedId")
            }
            .addOnFailureListener {
                Log.e("Firestore", "Error al guardar:", it)
            }
    }

    fun deleteAll() {
        val uid = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(uid)
            .collection("contacts")
            .get()
            .addOnSuccessListener { snapshot ->
                val batch = firestore.batch()

                for (doc in snapshot.documents) {
                    batch.delete(doc.reference)
                }

                batch.commit()
                    .addOnSuccessListener {
                        Log.d("Firestore", "Todos los contactos borrados.")
                        _contacts.value = emptyList()
                    }
            }
            .addOnFailureListener {
                Log.e("Firestore", "Error al borrar:", it)
            }
    }

    fun deleteContact(contact: Contact) {
        val uid = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(uid)
            .collection("contacts")
            .document(contact.id)
            .delete()
            .addOnSuccessListener {
                Log.d("Firestore", "Contacto eliminado: ${contact.name}")

                _contacts.value = _contacts.value.filter { it.id != contact.id }
            }
            .addOnFailureListener {
                Log.e("Firestore", "Error al eliminar contacto:", it)
            }
    }

    fun togglePaid(contactId: String) {
        val uid = auth.currentUser?.uid ?: return

        val currentContact =
            _contacts.value.firstOrNull { it.id == contactId } ?: return

        firestore.collection("users")
            .document(uid)
            .collection("contacts")
            .document(contactId)
            .update("isPaid", !currentContact.isPaid)
            .addOnFailureListener {
                Log.e("Firestore", "Error al actualizar estado:", it)
            }
    }
}

