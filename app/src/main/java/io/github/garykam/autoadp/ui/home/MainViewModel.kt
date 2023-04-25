package io.github.garykam.autoadp.ui.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import io.github.garykam.autoadp.utils.Utils
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel : ViewModel() {
    private var username = mutableStateOf(Utils.getUsername())
    private var password = mutableStateOf(Utils.getPassword())
    private var time = mutableStateOf(Utils.getTime())

    fun getUsername() = username.value

    fun getPassword() = password.value

    fun getHour() = time.value.split(':')[0].toInt()

    fun getMinute() = time.value.split(':')[1].toInt()

    fun getTime(): String {
        val dateFormat = SimpleDateFormat("h:mm a", Locale.US)
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, getHour())
            set(Calendar.MINUTE, getMinute())
        }

        return dateFormat.format(calendar.time)
    }

    fun setUsername(_username: String) {
        username.value = _username
    }

    fun setPassword(_password: String) {
        password.value = _password
    }

    fun saveCredentials() {
        Utils.saveCredentials(username.value, password.value)
    }

    fun saveTime(_time: String) {
        time.value = _time
        Utils.saveTime(_time)
    }
}