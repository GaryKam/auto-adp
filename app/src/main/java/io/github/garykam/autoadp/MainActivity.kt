package io.github.garykam.clocker

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Utils.setContext(applicationContext)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECEIVE_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(Utils.TAG, "MainActivity#requestPermission")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECEIVE_SMS), 69)
        }

        /*if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SYSTEM_ALERT_WINDOW
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(Utils.TAG, "MainActivity#requestPermission")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.SYSTEM_ALERT_WINDOW),
                70
            )
        }

        startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))*/

        setContent {
            MainScreen(
                onClockOut = { startActivity(Intent(this, AdpActivity::class.java)) },
                onSave = { username, password ->
                    Utils.saveCredentials(username, password)
                    Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show()
                },
                scheduleClockOut = {
                    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val pendingIntent = PendingIntent.getBroadcast(
                        this,
                        0,
                        Intent(this, AlarmBroadcastReceiver::class.java),
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + 7000L,
                        pendingIntent
                    )
                    Log.d(Utils.TAG, "scheduled alarm")
                })
        }
    }
}