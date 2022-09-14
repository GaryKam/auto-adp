package io.github.garykam.clocker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.garykam.clocker.ui.theme.AppTheme
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ClockHelper.loadSchedule(this, mainViewModel)

        setContent {
            AppTheme {
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
                        ClockerButton(mainViewModel.isClockedIn()) {
                            mainViewModel.clockInOut()
                            ClockHelper.handleClockOption(
                                this@MainActivity,
                                mainViewModel
                            )
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
            style = MaterialTheme.typography.h2,
        )
    }

    @Composable
    private fun ClockerSchedule() {
        for ((name, time) in mainViewModel.clockTimes.filterNot { it.key == ClockOption.MORNING_OUT.name }) {
            val text = "$name: " + if (time.isEmpty()) {
                time
            } else {
                val date = SimpleDateFormat("HH:mm:ss.SSS", Locale.US).parse(time)!!
                SimpleDateFormat("h:mm a", Locale.US).format(date)
            }

            Text(
                text = text,
                style = MaterialTheme.typography.body1
            )
        }

    }

    @Composable
    private fun ClockerText(clockOption: ClockOption) {
        Text(
            text = clockOption.getText(this),
            style = MaterialTheme.typography.h5
        )
    }

    @Composable
    private fun ClockerButton(
        clockedIn: Boolean,
        onClockChange: () -> Unit
    ) {
        AnimatedVisibility(visible = mainViewModel.isClockButtonVisible()) {
            Button(onClick = { onClockChange() }) {
                Text(
                    text = if (clockedIn) getString(R.string.clock_out) else getString(R.string.clock_in),
                    style = MaterialTheme.typography.button
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
                        AlarmHelper.dismissAlarm(this)
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
}
