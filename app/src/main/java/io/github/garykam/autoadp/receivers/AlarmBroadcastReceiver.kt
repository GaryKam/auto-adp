package io.github.garykam.autoadp.receivers

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import io.github.garykam.autoadp.ui.adp.AdpActivity
import io.github.garykam.autoadp.utils.NotificationUtil
import io.github.garykam.autoadp.utils.PreferencesUtil

class AlarmBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(PreferencesUtil.TAG, "AlarmBroadcastReceiver#onReceive")

        PendingIntent.getActivity(
            context,
            0,
            Intent(context, AdpActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        ).run {
            NotificationUtil.createNotification(context, this)
        }
    }
}