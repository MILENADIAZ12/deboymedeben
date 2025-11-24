package com.tuempresa.deboymedebn.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tuempresa.deboymedebn.ui.components.ContactItem
import com.tuempresa.deboymedebn.viewmodel.ContactsViewModel
import com.tuempresa.deboymedebn.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    contactsViewModel: ContactsViewModel,
    authViewModel: AuthViewModel,
    currentScreen: String,
    onNavigateToCalendar: () -> Unit,
    onAddClick: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onLogout: () -> Unit
) {

    // cargar contactos
    LaunchedEffect(Unit) {
        contactsViewModel.loadContacts()
    }

    val contacts by contactsViewModel.contacts.collectAsState()
    var searchText by remember { mutableStateOf("") }
    var showMeDeben by remember { mutableStateOf(true) }

    // email del usuario actual
    val currentEmail = authViewModel.currentUserEmail()

    val filtered = contacts
        .filter { it.amount >= 0.0 }
        .filter { c ->
            val typeOk = if (showMeDeben) c.type == "meDeben" else c.type == "debo"
            val searchOk = searchText.isBlank() ||
                    c.name.contains(searchText, ignoreCase = true)
            typeOk && searchOk
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Registros") },
                actions = {
                    IconButton(onClick = onNavigateToNotifications) {
                        Icon(Icons.Default.Notifications, "Notificaciones")
                    }
                    IconButton(onClick = { contactsViewModel.deleteAll() }) {
                        Icon(Icons.Default.Delete, "Eliminar todo")
                    }
                }
            )
        },

        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, "Agregar")
            }
        },

        bottomBar = {

            var showMenu by remember { mutableStateOf(false) }

            Column {
                NavigationBar {

                    // HOME
                    NavigationBarItem(
                        selected = currentScreen == "home",
                        onClick = {},
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") }
                    )

                    // PERFIL sencillo sin pantalla
                    NavigationBarItem(
                        selected = false,
                        onClick = { showMenu = true },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                        label = { Text("Perfil") }
                    )

                    // CALENDARIO
                    NavigationBarItem(
                        selected = currentScreen == "calendar",
                        onClick = onNavigateToCalendar,
                        icon = { Icon(Icons.Default.DateRange, contentDescription = "Calendario") },
                        label = { Text("Calendario") }
                    )
                }

             //opciones de perfil tiene un despliegue de dos opciones (el correo de la cuenta,cerrar secion)
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Correo: $currentEmail") },
                        onClick = {}
                    )

                    DropdownMenuItem(
                        text = { Text("Cerrar sesión") },
                        onClick = {
                            showMenu = false
                            onLogout()
                        }
                    )
                }
            }
        }




    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {

            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Buscar contacto") }
            )

            Spacer(Modifier.height(16.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { showMeDeben = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            if (showMeDeben) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.secondary
                    )
                ) { Text("Me Deben") }

                Button(
                    onClick = { showMeDeben = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            if (!showMeDeben) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.secondary
                    )
                ) { Text("Debo") }
            }

            Spacer(Modifier.height(16.dp))

            if (filtered.isEmpty()) {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay registros")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(filtered) { contact ->
                        ContactItem(contact = contact, viewModel = contactsViewModel)
                    }
                }
            }
        }
    }
}
