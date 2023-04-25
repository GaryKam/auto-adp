package io.github.garykam.autoadp.ui.home

import android.Manifest
import android.app.AlarmManager
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.github.garykam.autoadp.R
import io.github.garykam.autoadp.receivers.SmsBroadcastReceiver
import io.github.garykam.autoadp.ui.screens.MainScreen
import io.github.garykam.autoadp.utils.Utils

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Utils.initSharedPreferences(applicationContext)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECEIVE_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(Utils.TAG, "MainActivity#requestPermission")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECEIVE_SMS), 0)
        }

        setContent {
            MainScreen(
                onSaveCredentials = {
                    Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show()
                },
                onScheduleClockOut = { time ->
                    val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        time,
                        SmsBroadcastReceiver.newIntent(this)
                    )

                    Toast.makeText(this, R.string.scheduled_clock_out, Toast.LENGTH_SHORT).show()
                })
        }
    }
}