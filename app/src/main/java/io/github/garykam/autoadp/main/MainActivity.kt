package io.github.garykam.autoadp.main

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.github.garykam.autoadp.R
import io.github.garykam.autoadp.receivers.AlarmReceiver
import io.github.garykam.autoadp.ui.theme.AppTheme
import io.github.garykam.autoadp.utils.AlarmUtil
import io.github.garykam.autoadp.utils.NotificationUtil
import io.github.garykam.autoadp.utils.PreferencesUtil

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NotificationUtil.initNotificationChannel(applicationContext)
        PreferencesUtil.initSharedPreferences(applicationContext)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECEIVE_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "requestPermission")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECEIVE_SMS), 0)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            with(getSystemService(ALARM_SERVICE) as AlarmManager) {
                if (!this.canScheduleExactAlarms()) {
                    Log.d(TAG, "requestScheduleExactAlarms")
                    Intent(
                        Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse(
                            "package:$packageName"
                        )
                    ).also {
                        startActivity(it)
                    }
                }
            }
        }

        setContent {
            AppTheme {
                MainScreen(
                    onSaveCredentials = {
                        Toast.makeText(this, R.string.saved_credentials, Toast.LENGTH_SHORT).show()
                    },
                    onScheduleClockOut = { time ->
                        AlarmUtil.scheduleAlarm(this, AlarmReceiver.newIntent(this), time)

                        Toast.makeText(this, R.string.scheduled_clock_out, Toast.LENGTH_SHORT)
                            .show()
                    },
                    onCancelClockOut = {
                        AlarmUtil.cancelAlarm(this, AlarmReceiver.newIntent(this))

                        Toast.makeText(this, R.string.cancelled_clock_out, Toast.LENGTH_SHORT)
                            .show()
                    }
                )
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}