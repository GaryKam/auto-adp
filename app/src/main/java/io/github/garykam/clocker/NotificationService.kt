package io.github.garykam.clocker

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import java.util.*

class NotificationService : Service() {
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == STOP_SERVICE_ACTION) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return super.onStartCommand(intent, flags, startId)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, AdpActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationBuilder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Automatically clocked out")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setFullScreenIntent(pendingIntent, true)
                .setAutoCancel(true)

        NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            lightColor = android.graphics.Color.RED
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }.also {
            val notificationService =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationService.createNotificationChannel(it)
        }

        val notification = notificationBuilder.build()
        startForeground(Random().nextInt(), notification)

        return START_STICKY
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "clocker_channel_01"
        private const val NOTIFICATION_CHANNEL_NAME = "clocker_channel"
        private const val STOP_SERVICE_ACTION = "stop_service_action"

        fun createStopServiceIntent(context: Context): Intent {
            return Intent(context, NotificationService::class.java).apply {
                action = STOP_SERVICE_ACTION
            }
        }
    }
}