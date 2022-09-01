package io.github.garykam.clocker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.AlarmClock
import android.util.Log
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.garykam.clocker.ui.theme.AppTheme
import org.json.JSONObject
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences =
            applicationContext.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)

        setContent {
            AppTheme {
                //DismissAlarmDialog()

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.weight(2F),
                        contentAlignment = Alignment.Center
                    ) {
                        ClockerTitle()
                    }

                    Box(
                        modifier = Modifier.weight(0.2F),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        ClockerText(mainViewModel.clockOption)
                    }

                    Box(
                        modifier = Modifier.weight(1F),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        ClockerButton(mainViewModel.isClockedIn()) {
                            mainViewModel.clockInOut()
                            handleClockSchedule(mainViewModel.clockOption)
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
    private fun ClockerButton(clockedIn: Boolean, onClockChange: () -> Unit) {
        var visible by remember { mutableStateOf(true) }

        AnimatedVisibility(visible = visible) {
            Button(
                onClick = {
                    //visible = !visible
                    onClockChange()
                }
            ) {
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

    private fun handleClockSchedule(clockOption: ClockOption) {
        saveClockSchedule(clockOption)

        when (clockOption) {
            ClockOption.LUNCH_OUT -> {
                /*scheduleAlarm(Calendar.getInstance().also {
                    it.add(Calendar.HOUR, 1)
                })*/
            }

            ClockOption.LUNCH_IN -> {
                val morningClockMillis = readClockTime(ClockOption.MORNING_IN)
                val lunchClockMillis = readClockTime(ClockOption.LUNCH_IN)
                val millis = lunchClockMillis - morningClockMillis

                val hour = TimeUnit.MILLISECONDS.toHours(millis)
                val minute = TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(hour)
                val second = TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(minute) -
                        TimeUnit.HOURS.toSeconds(hour)

                Log.d(TAG, "time:$hour $minute $second")
            }

            ClockOption.EVENING_OUT -> {

            }

            else -> {}
        }
    }

    private fun saveClockSchedule(clockOption: ClockOption) {
        Log.d(TAG, "saving clock time ${clockOption.name}")
        val editor = sharedPreferences.edit()
        if (clockOption == ClockOption.MORNING_OUT) {
            editor.clear().apply()
            return
        }
        val schedule = sharedPreferences.getString(KEY_SCHEDULE, "{}")!!
        val json = JSONObject(schedule).apply {
            put(clockOption.name, Calendar.getInstance().timeInMillis)
        }
        editor.putString(KEY_SCHEDULE, json.toString())
        editor.apply()
    }

    private fun readClockTime(clockOption: ClockOption): Long {
        val schedule = sharedPreferences.getString(KEY_SCHEDULE, "{}")!!
        val json = JSONObject(schedule)

        return json.getLong(clockOption.name)
    }

    private fun scheduleAlarm(calendar: Calendar) {
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

    companion object {
        private const val TAG = "MainActivity"
        private const val SHARED_PREFERENCES = "io.github.garykam.clocker"
        private const val KEY_SCHEDULE = "key_schedule"
    }
}
