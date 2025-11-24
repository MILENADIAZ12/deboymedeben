package com.tuempresa.deboymedebn.notifications

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.tuempresa.deboymedebn.R

class DailyNotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {

        val notification = NotificationCompat.Builder(applicationContext, "daily_channel")
            .setContentTitle("Recordatorio")
            .setContentText("Revisa tus deudas y pagos pendientes.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        NotificationManagerCompat
            .from(applicationContext)
            .notify(1001, notification)

        return Result.success()
    }
}
