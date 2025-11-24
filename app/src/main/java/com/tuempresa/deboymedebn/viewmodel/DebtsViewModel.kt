package com.tuempresa.deboymedebn.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tuempresa.deboymedebn.model.Debt


class DebtsViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid

    // GUARDAR DEUDA
    fun addDebt(debt: Debt, onResult: (Boolean) -> Unit) {
        if (uid == null) { onResult(false); return }

        val docRef = db.collection("users")
            .document(uid)
            .collection("debts")
            .document()

        val debtWithId = debt.copy(id = docRef.id)

        docRef.set(debtWithId)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    //  OBTENER TODAS LAS DEUDAS DEL USUARIO
    fun getDebts(onResult: (List<Debt>) -> Unit) {
        if (uid == null) { onResult(emptyList()); return }

        db.collection("users")
            .document(uid)
            .collection("debts")
            .get()
            .addOnSuccessListener { snapshot ->
                val debts = snapshot.documents.mapNotNull {
                    it.toObject(Debt::class.java)
                }
                onResult(debts)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }
}
