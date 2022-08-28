package io.github.garykam.clocker

import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
                        modifier = Modifier.weight(1F),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        ClockerText(clockedIn = mainViewModel.clockedIn)
                    }

                    Box(
                        modifier = Modifier.weight(1F),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        ClockerButton(
                            getString(R.string.clock_in_morning)
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
    private fun ClockerText(clockedIn: Boolean) {
        Text(
            text = if (clockedIn) {
                getString(R.string.clocked_in)
            } else {
                getString(R.string.clocked_out)
            },
            modifier = Modifier.padding(bottom = 10.dp),
            style = MaterialTheme.typography.h5
        )
    }

    @Composable
    private fun ClockerButton(text: String, onClockChange: () -> Unit) {
        var visible by remember { mutableStateOf(true) }

        AnimatedVisibility(visible = visible) {
            Button(
                onClick = {
                    visible = !visible
                    scheduleAlarm()
                    onClockChange()
                }
            ) {
                Text(text = text, fontSize = 20.sp)
            }
        }
    }

    private fun scheduleAlarm() {
        val calendar = Calendar.getInstance().also {
            it.add(Calendar.HOUR, 1)
        }

        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_HOUR, calendar.get(Calendar.HOUR_OF_DAY))
            putExtra(AlarmClock.EXTRA_MINUTES, calendar.get(Calendar.MINUTE))
            putExtra(AlarmClock.EXTRA_MESSAGE, "Clock-Out")
            putExtra(AlarmClock.EXTRA_VIBRATE, true)
            putExtra(AlarmClock.EXTRA_SKIP_UI, true)
        }

        startActivity(intent)
    }
}
