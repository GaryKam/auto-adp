package io.github.garykam.autoadp.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import io.github.garykam.autoadp.R
import java.util.*

object NotificationUtil {
    private var notificationId = -1
    private const val AUTO_ADP_CHANNEL_ID = "auto_adp_channel"

    fun initNotificationChannel(context: Context) {
        getNotificationManager(context).createNotificationChannel(
            NotificationChannel(
                AUTO_ADP_CHANNEL_ID,
                context.getString(R.string.auto_clock),
                NotificationManager.IMPORTANCE_HIGH
            )
        )
    }

    fun createNotification(context: Context, intent: PendingIntent) {
        NotificationCompat.Builder(context, AUTO_ADP_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(context.getString(R.string.auto_clock_out))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(intent)
            .setFullScreenIntent(intent, true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .build()
            .run {
                notificationId = Random().nextInt()
                getNotificationManager(context).notify(notificationId, this)
            }
    }

    fun clearNotification(context: Context) {
        getNotificationManager(context).cancel(notificationId)
    }

    private fun getNotificationManager(context: Context) =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
}