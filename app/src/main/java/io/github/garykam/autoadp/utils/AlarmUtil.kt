package io.github.garykam.autoadp.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context

object AlarmUtil {
    fun scheduleAlarm(context: Context, alarm: PendingIntent, time: Long) {
        getAlarmManager(context).setExact(
            AlarmManager.RTC_WAKEUP,
            time,
            alarm
        )
    }

    fun cancelAlarm(context: Context, alarm: PendingIntent) {
        getAlarmManager(context).cancel(alarm)
    }

    private fun getAlarmManager(context: Context) =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
}