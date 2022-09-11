package io.github.garykam.clocker

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var openAlarmDialog: Boolean by mutableStateOf(false)
    var clockOption: ClockOption by mutableStateOf(ClockOption.MORNING_OUT)

    fun clockInOut() {
        clockOption = clockOption.getNext()
    }

    fun isClockedIn(): Boolean =
        clockOption == ClockOption.MORNING_IN || clockOption == ClockOption.LUNCH_IN

    fun isClockButtonVisible() = clockOption != ClockOption.EVENING_OUT
}