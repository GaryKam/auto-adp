package io.github.garykam.clocker

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var openAlarmDialog: Boolean by mutableStateOf(false)
    var clockTime: ClockTime by mutableStateOf(ClockTime.MORNING_OUT)
        private set

    fun clockInOut() {
        clockTime = clockTime.getNext()
    }

    fun isClockedIn(): Boolean =
        clockTime == ClockTime.MORNING_IN || clockTime == ClockTime.LUNCH_IN
}