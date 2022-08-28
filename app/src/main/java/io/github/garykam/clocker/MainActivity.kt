package io.github.garykam.clocker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.garykam.clocker.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ClockerTitle()
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        ClockerText(mainViewModel.clockedIn)
                        ClockerButton(
                            mainViewModel.clockedIn,
                            onClockChange = { mainViewModel.clockInOut() })
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
            text = if (clockedIn) getString(R.string.clocked_in) else getString(R.string.clocked_out),
            modifier = Modifier.padding(bottom = 10.dp),
            style = MaterialTheme.typography.h6
        )
    }

    @Composable
    private fun ClockerButton(clockedIn: Boolean, onClockChange: () -> Unit) {
        var widthState by remember { mutableStateOf(100.dp) }
        val width by animateDpAsState(
            targetValue = widthState,
            tween(durationMillis = ANIMATION_DURATION)
        )

        Button(
            onClick = {
                onClockChange()
                widthState = 0.dp
            },
            modifier = Modifier.size(width, 40.dp)
        ) {
            if (clockedIn) {
                Text(text = getString(R.string.clock_out), softWrap = false)
            } else {
                Text(text = getString(R.string.clock_in), softWrap = false)
            }
        }
    }

    companion object {
        const val ANIMATION_DURATION = 500
    }
}
