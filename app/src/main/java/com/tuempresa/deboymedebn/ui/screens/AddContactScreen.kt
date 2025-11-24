package com.tuempresa.deboymedebn.ui.screens

import android.app.DatePickerDialog
import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tuempresa.deboymedebn.model.Contact
import com.tuempresa.deboymedebn.viewmodel.ContactsViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactScreen(
    contactsViewModel: ContactsViewModel,
    onSaveDone: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current

    // ✔ launcher nativo para pedir permisos
    var hasPermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(android.Manifest.permission.READ_CONTACTS)
    }

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }

    var type by remember { mutableStateOf("meDeben") }

    var showErrors by remember { mutableStateOf(false) }
    var contactResults by remember { mutableStateOf(listOf<Pair<String, String>>()) }

    // DatePicker
    val calendar = Calendar.getInstance()
    val datePicker = DatePickerDialog(
        context,
        { _, y, m, d -> dueDate = "$d/${m + 1}/$y" },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar contacto") },
                navigationIcon = {
                    Text(
                        "←",
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable { onCancel() }
                    )
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

           //buscar contacto
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    if (hasPermission) {
                        contactResults = searchContacts(context, it)
                    }
                },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )

            if (contactResults.isNotEmpty()) {
                LazyColumn(modifier = Modifier.height(150.dp)) {
                    items(contactResults) { item ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable {
                                    name = item.first
                                    phone = item.second
                                    contactResults = emptyList()
                                }
                                .padding(8.dp)
                        ) {
                            Text("${item.first} - ${item.second}")
                        }
                    }
                }
            }

         //campos para agregar contacto
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Teléfono") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text("Monto") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // =============================
            // FECHA EDITABLE + DATEPICKER
            // =============================
            // --- FECHA CON TRES DESPLEGABLES ---
            val days = (1..31).toList()
            val months = (1..12).toList()
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            val years = (currentYear..currentYear + 5).toList()

            var selectedDay by remember { mutableStateOf<Int?>(null) }
            var selectedMonth by remember { mutableStateOf<Int?>(null) }
            var selectedYear by remember { mutableStateOf<Int?>(null) }

            var expandedDay by remember { mutableStateOf(false) }
            var expandedMonth by remember { mutableStateOf(false) }
            var expandedYear by remember { mutableStateOf(false) }


            LaunchedEffect(selectedDay, selectedMonth, selectedYear) {
                if (selectedDay != null && selectedMonth != null && selectedYear != null) {
                    dueDate = "${selectedDay}/${selectedMonth}/${selectedYear}"
                }
            }

            Text("Fecha límite", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(4.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

                // dia
                Box {
                    OutlinedButton(
                        onClick = { expandedDay = true },
                        modifier = Modifier.width(100.dp)
                    ) {
                        Text(selectedDay?.toString() ?: "Día")
                    }

                    DropdownMenu(
                        expanded = expandedDay,
                        onDismissRequest = { expandedDay = false }
                    ) {
                        days.forEach { d ->
                            DropdownMenuItem(
                                text = { Text(d.toString()) },
                                onClick = {
                                    selectedDay = d
                                    expandedDay = false
                                }
                            )
                        }
                    }
                }

                // mes
                Box {
                    OutlinedButton(
                        onClick = { expandedMonth = true },
                        modifier = Modifier.width(100.dp)
                    ) {
                        Text(selectedMonth?.toString() ?: "Mes")
                    }

                    DropdownMenu(
                        expanded = expandedMonth,
                        onDismissRequest = { expandedMonth = false }
                    ) {
                        months.forEach { m ->
                            DropdownMenuItem(
                                text = { Text(m.toString()) },
                                onClick = {
                                    selectedMonth = m
                                    expandedMonth = false
                                }
                            )
                        }
                    }
                }

                //año
                Box {
                    OutlinedButton(
                        onClick = { expandedYear = true },
                        modifier = Modifier.width(120.dp)
                    ) {
                        Text(selectedYear?.toString() ?: "Año")
                    }

                    DropdownMenu(
                        expanded = expandedYear,
                        onDismissRequest = { expandedYear = false }
                    ) {
                        years.forEach { y ->
                            DropdownMenuItem(
                                text = { Text(y.toString()) },
                                onClick = {
                                    selectedYear = y
                                    expandedYear = false
                                }
                            )
                        }
                    }
                }
            }


            Spacer(Modifier.height(8.dp))

           //diseño de botones
            Row {
                RadioButton(
                    selected = type == "meDeben",
                    onClick = { type = "meDeben" }
                )
                Text("Me deben")

                Spacer(Modifier.width(16.dp))

                RadioButton(
                    selected = type == "debo",
                    onClick = { type = "debo" }
                )
                Text("Debo")
            }

           //valdar si se lleno la info del contacto
            if (showErrors && name.isBlank())
                Text("El nombre es obligatorio", color = MaterialTheme.colorScheme.error)

            if (showErrors && phone.isBlank())
                Text("El teléfono es obligatorio", color = MaterialTheme.colorScheme.error)

            Spacer(Modifier.height(16.dp))

       //guardar
            Button(
                onClick = {

                    if (name.isBlank() || phone.isBlank()) {
                        showErrors = true
                        return@Button
                    }

                    val amountFinal = amountText.toDoubleOrNull() ?: 0.0

                    val newContact = Contact(
                        name = name,
                        phone = phone,
                        description = description,
                        amount = amountFinal,
                        dueDate = dueDate,
                        type = type,
                        isPaid = false
                    )

                    contactsViewModel.addContact(newContact)
                    onSaveDone()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar contacto")
            }
        }
    }
}

//Buscar contactos del celular
fun searchContacts(context: Context, query: String): List<Pair<String, String>> {
    if (query.isBlank()) return emptyList()

    val list = mutableListOf<Pair<String, String>>()

    val cursor: Cursor? = context.contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        null,
        "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ?",
        arrayOf("%$query%"),
        null
    )

    cursor?.use {
        val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
        val phoneIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

        while (it.moveToNext()) {
            val name = it.getString(nameIndex)
            val phone = it.getString(phoneIndex)
            list.add(name to phone)
        }
    }

    return list
}
