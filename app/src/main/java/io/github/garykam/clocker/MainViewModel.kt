package io.github.garykam.clocker

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var clockedIn: Boolean by mutableStateOf(false)
        private set
    var openAlarmDialog: Boolean by mutableStateOf(false)

    fun clockInOut() {
        Handler(Looper.getMainLooper()).postDelayed({ clockedIn = !clockedIn }, 500L)
    }
}