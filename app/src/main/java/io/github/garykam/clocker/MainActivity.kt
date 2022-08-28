package io.github.garykam.clocker

import android.content.Intent
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.garykam.clocker.ui.theme.AppTheme
import java.util.*

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                DismissAlarmDialog()

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
                        ClockerText(mainViewModel.clockTime)
                    }

                    Box(
                        modifier = Modifier.weight(1F),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        ClockerButton(
                            mainViewModel.isClockedIn(),
                            mainViewModel.clockTime
                        ) { mainViewModel.clockInOut() }
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
    private fun ClockerText(clockTime: ClockTime) {
        Text(
            text = clockTime.getText(this),
            modifier = Modifier
                .padding(bottom = 10.dp)
                .clickable(enabled = true) { mainViewModel.openAlarmDialog = true },
            style = MaterialTheme.typography.h5
        )
    }

    @Composable
    private fun ClockerButton(clockedIn: Boolean, clockTime: ClockTime, onClockChange: () -> Unit) {
        var visible by remember { mutableStateOf(true) }

        AnimatedVisibility(visible = visible) {
            Button(
                onClick = {
                    //visible = !visible
                    onClockChange()
                    scheduleAlarm(clockTime)
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

    private fun scheduleAlarm(clockTime: ClockTime) {
        if (clockTime == ClockTime.MORNING_OUT || clockTime == ClockTime.MORNING_IN) {
            val calendar = Calendar.getInstance().also {
                it.add(Calendar.HOUR, if (clockTime == ClockTime.MORNING_OUT) 9 else 1)
            }

            val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                putExtra(AlarmClock.EXTRA_HOUR, calendar.get(Calendar.HOUR_OF_DAY))
                putExtra(AlarmClock.EXTRA_MINUTES, calendar.get(Calendar.MINUTE))
                putExtra(AlarmClock.EXTRA_MESSAGE, getString(R.string.app_name))
                putExtra(AlarmClock.EXTRA_VIBRATE, true)
                putExtra(AlarmClock.EXTRA_SKIP_UI, true)
            }

            startActivity(intent)
        }
    }

    private fun dismissAlarm() {
        val intent = Intent(AlarmClock.ACTION_DISMISS_ALARM).apply {
            putExtra(AlarmClock.EXTRA_ALARM_SEARCH_MODE, AlarmClock.ALARM_SEARCH_MODE_LABEL)
            putExtra(AlarmClock.EXTRA_MESSAGE, getString(R.string.app_name))
        }

        startActivity(intent)
    }
}
