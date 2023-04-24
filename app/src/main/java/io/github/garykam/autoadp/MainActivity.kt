package io.github.garykam.autoadp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.github.garykam.autoadp.screens.MainScreen
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
                onSave = { username, password ->
                    Utils.saveCredentials(username, password)
                    Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show()
                })
        }
    }
}
/*
val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + 7000L,
                        SmsBroadcastReceiver.intent(this)
                    )
 */