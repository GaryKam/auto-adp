package io.github.garykam.clocker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.provider.AlarmClock

object AlarmHelper {
    fun setTimer(context: Context, length: Int) {
        val intent = Intent(AlarmClock.ACTION_SET_TIMER).apply {
            putExtra(AlarmClock.EXTRA_LENGTH, length)
            putExtra(AlarmClock.EXTRA_MESSAGE, context.getString(R.string.app_name))
            putExtra(AlarmClock.EXTRA_SKIP_UI, true)
        }

        context.startActivity(intent)
    }

    fun setBroadcast(context: Context, time: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, createBroadcastIntent(context))
    }

    fun cancelBroadcast(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(createBroadcastIntent(context))
    }

    private fun createBroadcastIntent(context: Context): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, AlarmReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
    }
}