package io.github.garykam.clocker

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var clockedIn: Boolean by mutableStateOf(false)
        private set

    fun clockInOut() {
        Handler(Looper.getMainLooper()).postDelayed(
            {
                clockedIn = !clockedIn
            },
            MainActivity.ANIMATION_DURATION.toLong()
        )
    }
}