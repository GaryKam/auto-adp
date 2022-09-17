package io.github.garykam.clocker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val i = Intent(it, NotificationService::class.java)
            it.startForegroundService(i)
        }
    }
}