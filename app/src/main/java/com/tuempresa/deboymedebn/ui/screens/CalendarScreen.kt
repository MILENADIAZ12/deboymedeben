package com.tuempresa.deboymedebn.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.tuempresa.deboymedebn.model.Contact
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun CalendarScreen(
    contacts: List<Contact>,
    onBack: () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    val contactsByDate = contacts
        .filter { it.dueDate.isNotBlank() }
        .mapNotNull {
            try {
                LocalDate.parse(it.dueDate, formatter) to it
            } catch (_: Exception) {
                null
            }
        }
        .groupBy({ it.first }, { it.second })

    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calendario de Fechas Límite") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(12.dp)
        ) {

            val currentMonth = remember { YearMonth.now() }
            val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }

            val calendarState = rememberCalendarState(
                startMonth = currentMonth.minusMonths(12),
                endMonth = currentMonth.plusMonths(12),
                firstVisibleMonth = currentMonth,
                firstDayOfWeek = firstDayOfWeek
            )

            HorizontalCalendar(
                state = calendarState,
                dayContent = { day: CalendarDay ->
                    val date = day.date
                    val hasEvents = contactsByDate.containsKey(date)

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { selectedDate = date }
                            .background(
                                when {
                                    selectedDate == date -> Color(0xFF1976D2)
                                    hasEvents -> Color(0xFFFFF59D)
                                    else -> Color.Transparent
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = date.dayOfMonth.toString(),
                            color = if (selectedDate == date) Color.White else Color.Black
                        )
                    }
                }
            )

            Spacer(Modifier.height(16.dp))

            Text(
                "Registros del día:",
                style = MaterialTheme.typography.titleMedium
            )

            val listForDay = selectedDate?.let { contactsByDate[it] } ?: emptyList()

            if (listForDay.isEmpty()) {
                Text(
                    "No hay pagos para este día",
                    modifier = Modifier.padding(top = 8.dp),
                    color = Color.Gray
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(listForDay) { contact ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFFE3F2FD),
                            tonalElevation = 2.dp,
                            shadowElevation = 2.dp
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(contact.name, style = MaterialTheme.typography.titleMedium)
                                Text("Monto: $${contact.amount}")
                                Text("Teléfono: ${contact.phone}")
                                Text("Tipo: ${if (contact.type == "meDeben") "Me deben" else "Debo"}")
                            }
                        }
                    }
                }
            }
        }
    }
}
