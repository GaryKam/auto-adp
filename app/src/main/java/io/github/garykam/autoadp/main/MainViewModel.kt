package io.github.garykam.autoadp.main

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import io.github.garykam.autoadp.utils.PreferencesUtil
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel : ViewModel() {
    private var username = mutableStateOf(PreferencesUtil.getUsername())
    private var password = mutableStateOf(PreferencesUtil.getPassword())
    private var time = mutableStateOf(PreferencesUtil.getTime())
    private var clockOutScheduled = mutableStateOf(PreferencesUtil.isClockOutScheduled())

    fun getUsername() = username.value

    fun getPassword() = password.value

    fun getHour() = time.value.split(':')[0].toInt()

    fun getMinute() = time.value.split(':')[1].toInt()

    fun getTime(): Long {
        with (Calendar.getInstance()) {
            set(Calendar.HOUR_OF_DAY, getHour())
            set(Calendar.MINUTE, getMinute())
            return timeInMillis
        }
    }

    fun getDisplayTime(): String {
        return SimpleDateFormat("h:mm a", Locale.US).format(Date(getTime()))
    }

    fun isClockOutScheduled() = clockOutScheduled.value

    fun setUsername(_username: String) {
        username.value = _username
    }

    fun setPassword(_password: String) {
        password.value = _password
    }

    fun setCredentials() {
        PreferencesUtil.saveCredentials(username.value, password.value)
    }

    fun setTime(_time: String) {
        time.value = _time
        PreferencesUtil.saveTime(_time)
    }

    fun setClockOutScheduled(_clockOutScheduled: Boolean) {
        clockOutScheduled.value = _clockOutScheduled
        PreferencesUtil.saveClockOutScheduled(_clockOutScheduled)
    }
}