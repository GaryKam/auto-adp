package io.github.garykam.autoadp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import io.github.garykam.autoadp.utils.Utils
import java.util.*

class AlarmBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(Utils.TAG, "AlarmBroadcastReceiver#onReceive")

        val fullScreenIntent = Intent(context, AdpActivity::class.java)
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context, 0,
            fullScreenIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, AUTO_ADP_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(context.getString(R.string.clock_out))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(fullScreenPendingIntent)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(
            NotificationChannel(
                AUTO_ADP_CHANNEL_ID,
                context.getString(R.string.auto_clock),
                NotificationManager.IMPORTANCE_HIGH
            )
        )

        notificationManager.notify(Random().nextInt(), builder.build())
    }

    companion object {
        private const val AUTO_ADP_CHANNEL_ID = "auto_adp_channel"
    }
}