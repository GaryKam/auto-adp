package io.github.garykam.clocker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.AlarmClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.garykam.clocker.ui.theme.AppTheme
import org.json.JSONObject
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    private val clockTimes: MutableMap<String, String> =
        ClockOption.values().map { it.name }.associateWith { "" }.toMutableMap()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences =
            applicationContext.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        //sharedPreferences.edit().remove(KEY_DATE).apply()
        updateSchedule()

        setContent {
            AppTheme {
                DismissAlarmDialog()

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.weight(2f),
                        contentAlignment = Alignment.Center
                    ) {
                        ClockerTitle()
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.Start
                    ) {
                        ClockerSchedule()
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ClockerText(mainViewModel.clockOption)
                        ClockerButton(mainViewModel.isClockedIn(), mainViewModel.clockOption) {
                            mainViewModel.clockInOut()
                            handleClockOption(mainViewModel.clockOption)
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun ClockerTitle() {
        Text(
            text = getString(R.string.app_name).uppercase(),
            style = MaterialTheme.typography.h2
        )
    }

    @Composable
    private fun ClockerSchedule() {
        for ((name, time) in clockTimes.filterNot { it.key == ClockOption.MORNING_OUT.name }) {
            Text(text = "$name: $time")
        }
    }

    @Composable
    private fun ClockerText(clockOption: ClockOption) {
        Text(
            text = clockOption.getText(this),
            modifier = Modifier
                .padding(bottom = 10.dp)
                .clickable(enabled = true) { mainViewModel.openAlarmDialog = true },
            style = MaterialTheme.typography.h5
        )
    }

    @Composable
    private fun ClockerButton(clockedIn: Boolean, clockOption: ClockOption, onClockChange: () -> Unit) {
        AnimatedVisibility(visible = clockOption != ClockOption.EVENING_OUT) {
            Button(onClick = { onClockChange() }) {
                Text(
                    text = if (clockedIn) getString(R.string.clock_out) else getString(R.string.clock_in),
                    fontSize = 20.sp
                )
            }
        }
    }

    @Composable
    private fun DismissAlarmDialog() {
        if (mainViewModel.openAlarmDialog) {
            AlertDialog(
                onDismissRequest = { mainViewModel.openAlarmDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        mainViewModel.openAlarmDialog = false
                        dismissAlarm()
                    }) {
                        Text(text = getString(R.string.yes))
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        mainViewModel.openAlarmDialog = false
                    }) {
                        Text(text = getString(R.string.no))
                    }
                },
                title = { Text(text = getString(R.string.alarm_delete)) },
                text = { Text(text = getString(R.string.alarm_delete_confirmation)) }
            )
        }
    }

    private fun updateSchedule() {
        val localDate = LocalDate.now().toString()
        if (sharedPreferences.getString(KEY_DATE, "") == localDate) {
            for (clockOption in ClockOption.values().slice(1..4)) {
                val clockTime = readFromSchedule(clockOption)?.toString() ?: ""

                if (clockTime.isNotEmpty()) {
                    clockTimes[clockOption.name] = clockTime
                } else {
                    mainViewModel.clockOption = clockOption.getPrevious()
                    break
                }
            }
        } else {
            sharedPreferences.edit().apply {
                remove(KEY_SCHEDULE)
                putString(KEY_DATE, localDate)
                apply()
            }
        }
    }

    private fun handleClockOption(clockOption: ClockOption) {
        saveToSchedule(clockOption)

        when (clockOption) {
            ClockOption.MORNING_OUT, ClockOption.MORNING_IN -> return

            ClockOption.LUNCH_OUT -> setTimer(TimeUnit.HOURS.toSeconds(1).toInt())

            ClockOption.LUNCH_IN -> {
                val timeWorked = Duration.between(
                    readFromSchedule(ClockOption.MORNING_IN), readFromSchedule(ClockOption.LUNCH_IN)
                )
                val timeRemaining = LocalTime.of(9, 0).minus(timeWorked)

                setAlarm(Calendar.getInstance().apply {
                    add(Calendar.HOUR, timeRemaining.hour)
                    add(Calendar.MINUTE, timeRemaining.minute)
                    add(Calendar.SECOND, timeRemaining.second)
                })
            }

            ClockOption.EVENING_OUT -> sharedPreferences.edit().remove(KEY_DATE).apply()
        }
    }

    private fun saveToSchedule(clockOption: ClockOption) {
        val schedule = sharedPreferences.getString(KEY_SCHEDULE, "{}")!!
        val localTime = LocalTime.now().toString()
        val json = JSONObject(schedule).apply {
            put(clockOption.name, localTime)
        }

        sharedPreferences.edit().apply {
            putString(KEY_DATE, LocalDate.now().toString())
            putString(KEY_SCHEDULE, json.toString())
            apply()
        }

        clockTimes[clockOption.name] = localTime
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

    private fun setAlarm(calendar: Calendar) {
        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_HOUR, calendar.get(Calendar.HOUR_OF_DAY))
            putExtra(AlarmClock.EXTRA_MINUTES, calendar.get(Calendar.MINUTE))
            putExtra(AlarmClock.EXTRA_MESSAGE, getString(R.string.app_name))
            putExtra(AlarmClock.EXTRA_VIBRATE, true)
            putExtra(AlarmClock.EXTRA_SKIP_UI, true)
        }

        startActivity(intent)
    }

    private fun dismissAlarm() {
        val intent = Intent(AlarmClock.ACTION_DISMISS_ALARM).apply {
            putExtra(AlarmClock.EXTRA_ALARM_SEARCH_MODE, AlarmClock.ALARM_SEARCH_MODE_LABEL)
            putExtra(AlarmClock.EXTRA_MESSAGE, getString(R.string.app_name))
        }

        startActivity(intent)
    }

    private fun setTimer(length: Int) {
        val intent = Intent(AlarmClock.ACTION_SET_TIMER).apply {
            putExtra(AlarmClock.EXTRA_LENGTH, length)
            putExtra(AlarmClock.EXTRA_MESSAGE, getString(R.string.app_name))
            putExtra(AlarmClock.EXTRA_SKIP_UI, true)
        }

        startActivity(intent)
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val SHARED_PREFERENCES = "io.github.garykam.clocker"
        private const val KEY_DATE = "key_date"
        private const val KEY_SCHEDULE = "key_schedule"
    }
}
