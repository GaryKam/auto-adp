package io.github.garykam.clocker

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var clockOption: ClockOption by mutableStateOf(ClockOption.MORNING_OUT)
    var broadcastScheduleTime: String by mutableStateOf("")
    val clockTimes: MutableMap<String, String> =
        ClockOption.values().map { it.name }.associateWith { "" }.toMutableMap()

    fun clockInOut() {
        clockOption = clockOption.getNext()
    }

    fun isClockedIn() = clockOption == ClockOption.MORNING_IN || clockOption == ClockOption.LUNCH_IN

    fun isEndOfDay() = clockOption == ClockOption.EVENING_OUT

    fun isBroadcastScheduled() = broadcastScheduleTime.isNotEmpty()
}