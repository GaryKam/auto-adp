package io.github.garykam.autoadp.receivers

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import io.github.garykam.autoadp.ui.adp.AdpActivity
import io.github.garykam.autoadp.utils.NotificationUtil

class AlarmBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive")

        PendingIntent.getActivity(
            context,
            0,
            Intent(context, AdpActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        ).run {
            NotificationUtil.createNotification(context, this)
        }
    }

    companion object {
        private const val TAG = "AlarmBroadcastReceiver"
    }
}