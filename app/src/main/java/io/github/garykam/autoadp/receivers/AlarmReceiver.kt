package io.github.garykam.autoadp.receivers

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import io.github.garykam.autoadp.adp.AdpActivity
import io.github.garykam.autoadp.utils.NotificationUtil
import io.github.garykam.autoadp.utils.PreferencesUtil

class AlarmReceiver : BroadcastReceiver() {
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

        with(PreferencesUtil) {
            initSharedPreferences(context)
            saveClockOutScheduled(false)
        }
    }

    companion object {
        private const val TAG = "AlarmBroadcastReceiver"

        fun newIntent(context: Context): PendingIntent {
            return PendingIntent.getBroadcast(
                context,
                0,
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}