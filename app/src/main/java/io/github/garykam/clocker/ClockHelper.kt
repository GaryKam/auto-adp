package io.github.garykam.clocker

import android.content.Context
import org.json.JSONObject
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import java.util.concurrent.TimeUnit

object ClockHelper {
    private const val SHARED_PREFERENCES = "io.github.garykam.clocker"
    private const val KEY_DATE = "key_date"
    private const val KEY_SCHEDULE = "key_schedule"
    private const val KEY_BROADCAST_SCHEDULED = "key_broadcast_scheduled"

    fun loadSchedule(context: Context, mainViewModel: MainViewModel) {
        val sharedPreferences =
            context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val localDate = LocalDate.now().toString()

        if (sharedPreferences.getString(KEY_DATE, "") == localDate) {
            for (clockOption in ClockOption.values().slice(1..4)) {
                val clockTime = readFromSchedule(context, clockOption)?.toString() ?: ""

                if (clockTime.isNotEmpty()) {
                    mainViewModel.clockOption = clockOption
                    mainViewModel.clockTimes[clockOption.name] = clockTime
                    mainViewModel.isBroadcastScheduled =
                        sharedPreferences.getBoolean(KEY_BROADCAST_SCHEDULED, false)
                } else {
                    mainViewModel.clockOption = clockOption.getPrevious()
                    break
                }
            }
        } else {
            sharedPreferences.edit().apply {
                remove(KEY_SCHEDULE)
                putString(KEY_DATE, localDate)
                putBoolean(KEY_BROADCAST_SCHEDULED, false)
                apply()
            }
        }
    }

    fun handleClockOption(context: Context, mainViewModel: MainViewModel) {
        mainViewModel.clockInOut()

        when (mainViewModel.clockOption) {
            ClockOption.LUNCH_OUT -> AlarmHelper.setTimer(
                context,
                TimeUnit.HOURS.toSeconds(1).toInt()
            )

            ClockOption.LUNCH_IN -> {
                val timeWorked = Duration.between(
                    readFromSchedule(context, ClockOption.MORNING_IN),
                    LocalTime.now()
                )
                val timeRemaining = LocalTime.of(9, 0).minus(timeWorked)

                val time = Calendar.getInstance().apply {
                    add(Calendar.HOUR, timeRemaining.hour)
                    add(Calendar.MINUTE, timeRemaining.minute)
                    add(Calendar.SECOND, timeRemaining.second)
                }.timeInMillis

                AlarmHelper.setBroadcast(context, time)
                mainViewModel.isBroadcastScheduled = true
            }

            else -> {}
        }

        saveToSchedule(context, mainViewModel)
    }

    private fun readFromSchedule(context: Context, clockOption: ClockOption): LocalTime? {
        val sharedPreferences =
            context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val schedule = sharedPreferences.getString(KEY_SCHEDULE, "{}")!!
        val json = JSONObject(schedule)

        return if (json.has(clockOption.name)) {
            LocalTime.parse(json.getString(clockOption.name))
        } else {
            null
        }
    }

    private fun saveToSchedule(context: Context, mainViewModel: MainViewModel) {
        val sharedPreferences =
            context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val schedule = sharedPreferences.getString(KEY_SCHEDULE, "{}")!!
        val localTime = LocalTime.now().toString()
        val json = JSONObject(schedule).apply {
            put(mainViewModel.clockOption.name, localTime)
        }

        sharedPreferences.edit().apply {
            putString(KEY_DATE, LocalDate.now().toString())
            putString(KEY_SCHEDULE, json.toString())
            putBoolean(KEY_BROADCAST_SCHEDULED, mainViewModel.isBroadcastScheduled)
            apply()
        }

        mainViewModel.clockTimes[mainViewModel.clockOption.name] = localTime
    }
}