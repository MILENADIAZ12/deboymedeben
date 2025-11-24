package com.tuempresa.deboymedebn.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tuempresa.deboymedebn.model.Contact
import com.tuempresa.deboymedebn.viewmodel.ContactsViewModel

@Composable
fun ContactItem(
    contact: Contact,
    viewModel: ContactsViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }

    val scaleAnim by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scaleAnim)
            .background(Color.Transparent),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            // --- Nombre + monto ---
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = contact.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    text = "$${contact.amount}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(6.dp))

            // Fecha límite
            if (contact.dueDate.isNotBlank()) {
                Text(
                    "Fecha límite: ${contact.dueDate}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Descripción
            if (contact.description.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    contact.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Badge de estado
                AssistChip(
                    onClick = { },
                    label = {
                        Text(if (contact.isPaid) "Pagado" else "Pendiente")
                    },
                    leadingIcon = {
                        Icon(
                            if (contact.isPaid) Icons.Default.Check else Icons.Default.Warning,
                            contentDescription = null
                        )
                    }
                )

                Row {

                    // Botón PAGAR
                    if (!contact.isPaid) {
                        Button(
                            onClick = {
                                isPressed = true
                                showDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Pagar")


                            Spacer(Modifier.width(6.dp))
                            Text("Pagar")
                        }
                    }

                    Spacer(Modifier.width(10.dp))

                    // Botón ELIMINAR
                    IconButton(onClick = { viewModel.deleteContact(contact) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                    }
                }
            }
        }
    }

    // --- DIÁLOGO CONFIRMACIÓN ---
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmar pago") },
            text = { Text("¿Seguro que deseas marcar este ítem como pagado?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.togglePaid(contact.id)
                    showDialog = false
                    isPressed = false
                }) {
                    Text("Sí, pagar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    isPressed = false
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
