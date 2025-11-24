package com.tuempresa.deboymedebn

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.work.*
import com.tuempresa.deboymedebn.navigation.NavGraph
import com.tuempresa.deboymedebn.notifications.DailyNotificationWorker
import com.tuempresa.deboymedebn.viewmodel.AuthViewModel
import com.tuempresa.deboymedebn.viewmodel.ContactsViewModel
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Permiso de notificaciones
        askNotificationPermission()

        // Programar notificación diaria a las 8 de la noche
        scheduleDailyNotification()

        setContent {
            val navController = rememberNavController()
            val authViewModel: AuthViewModel = viewModel()
            val contactsViewModel: ContactsViewModel = viewModel()

            NavGraph(
                navController = navController,
                authViewModel = authViewModel,
                contactsViewModel = contactsViewModel
            )
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                100
            )
        }
    }

    private fun scheduleDailyNotification() {

        val now = Calendar.getInstance()
        val dueTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 19)
            set(Calendar.MINUTE, 10)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Si ya pasó la hora, programar para mañana
        if (dueTime.before(now)) {
            dueTime.add(Calendar.DAY_OF_YEAR, 1)
        }

        val initialDelay = dueTime.timeInMillis - now.timeInMillis

        // Worker repetitivo cada 24h
        val request = PeriodicWorkRequestBuilder<DailyNotificationWorker>(
            24, TimeUnit.HOURS
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()


        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "daily_notification_job",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}
