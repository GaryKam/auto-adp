package io.github.garykam.clocker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import java.util.*

object AlarmHelper {
    fun setAlarm(context: Context, calendar: Calendar) {
        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_HOUR, calendar.get(Calendar.HOUR_OF_DAY))
            putExtra(AlarmClock.EXTRA_MINUTES, calendar.get(Calendar.MINUTE))
            putExtra(AlarmClock.EXTRA_MESSAGE, context.getString(R.string.app_name))
            putExtra(AlarmClock.EXTRA_VIBRATE, true)
            putExtra(AlarmClock.EXTRA_SKIP_UI, true)
        }

        context.startActivity(intent)
    }

    fun dismissAlarm(context: Context) {
        val intent = Intent(AlarmClock.ACTION_DISMISS_ALARM).apply {
            putExtra(AlarmClock.EXTRA_ALARM_SEARCH_MODE, AlarmClock.ALARM_SEARCH_MODE_LABEL)
            putExtra(AlarmClock.EXTRA_MESSAGE, context.getString(R.string.app_name))
        }

        context.startActivity(intent)
    }

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
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, AlarmReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent)
    }
}