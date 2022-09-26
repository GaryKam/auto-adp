package io.github.garykam.clocker

import android.content.Context
import org.json.JSONObject
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import java.util.concurrent.TimeUnit

class ClockHelper(private val context: Context, private val mainViewModel: MainViewModel) {
    private val sharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)

    fun loadSchedule() {
        val localDate = LocalDate.now().toString()

        if (sharedPreferences.getString(KEY_DATE, "") == localDate) {
            for (clockOption in ClockOption.values().slice(1..4)) {
                val clockTime = readFromSchedule(clockOption)?.toString() ?: ""

                if (clockTime.isNotEmpty()) {
                    mainViewModel.clockOption = clockOption
                    mainViewModel.clockTimes[clockOption.name] = clockTime
                    mainViewModel.broadcastScheduleTime =
                        sharedPreferences.getString(KEY_BROADCAST_SCHEDULE_TIME, "")!!
                } else {
                    mainViewModel.clockOption = clockOption.getPrevious()
                    break
                }
            }
        } else {
            sharedPreferences.edit().apply {
                remove(KEY_SCHEDULE)
                putString(KEY_DATE, localDate)
                putString(KEY_BROADCAST_SCHEDULE_TIME, "")
                apply()
            }
        }
    }

    fun handleClockOption() {
        mainViewModel.clockInOut()

        when (mainViewModel.clockOption) {
            ClockOption.LUNCH_OUT -> AlarmHelper.setTimer(
                context,
                TimeUnit.HOURS.toSeconds(1).toInt()
            )

            ClockOption.LUNCH_IN -> {
                val timeWorked = Duration.between(
                    readFromSchedule(ClockOption.MORNING_IN),
                    LocalTime.now()
                )
                val timeRemaining = LocalTime.of(9, 0).minus(timeWorked)

                val calendar = Calendar.getInstance().apply {
                    add(Calendar.HOUR, timeRemaining.hour)
                    add(Calendar.MINUTE, timeRemaining.minute)
                    add(Calendar.SECOND, timeRemaining.second)
                }

                AlarmHelper.setBroadcast(context, calendar.timeInMillis)
                setBroadcastScheduled(calendar.time.toString())
            }

            else -> {}
        }

        saveToSchedule()
    }

    private fun readFromSchedule(clockOption: ClockOption): LocalTime? {
        val schedule = sharedPreferences.getString(KEY_SCHEDULE, "{}")!!
        val json = JSONObject(schedule)

        return if (json.has(clockOption.name)) {
            LocalTime.parse(json.getString(clockOption.name))
        } else {
            null
        }
    }

    private fun saveToSchedule() {
        val schedule = sharedPreferences.getString(KEY_SCHEDULE, "{}")!!
        val localTime = LocalTime.now().toString()
        val json = JSONObject(schedule).apply {
            put(mainViewModel.clockOption.name, localTime)
        }

        sharedPreferences.edit().apply {
            putString(KEY_DATE, LocalDate.now().toString())
            putString(KEY_SCHEDULE, json.toString())
            apply()
        }

        mainViewModel.clockTimes[mainViewModel.clockOption.name] = localTime
    }

    fun setBroadcastScheduled(broadcastScheduleTime: String) {
        mainViewModel.broadcastScheduleTime = broadcastScheduleTime
        sharedPreferences.edit().putString(KEY_BROADCAST_SCHEDULE_TIME, broadcastScheduleTime)
            .apply()
    }

    companion object {
        private const val SHARED_PREFERENCES = "io.github.garykam.clocker"
        private const val KEY_DATE = "key_date"
        private const val KEY_SCHEDULE = "key_schedule"
        private const val KEY_BROADCAST_SCHEDULE_TIME = "key_broadcast_schedule_time"
    }
}