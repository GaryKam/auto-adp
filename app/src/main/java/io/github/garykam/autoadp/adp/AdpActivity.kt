package io.github.garykam.autoadp.adp

import android.content.IntentFilter
import android.os.Bundle
import android.provider.Telephony
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import io.github.garykam.autoadp.receivers.SmsReceiver
import io.github.garykam.autoadp.ui.theme.AppTheme
import io.github.garykam.autoadp.utils.PreferencesUtil

class AdpActivity : ComponentActivity() {
    private val smsReceiver = SmsReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")

        PreferencesUtil.initSharedPreferences(applicationContext)

        setShowWhenLocked(true)
        setTurnScreenOn(true)

        setContent {
            AppTheme {
                AdpScreen()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        registerReceiver(smsReceiver, IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION))
    }

    override fun onPause() {
        super.onPause()

        unregisterReceiver(smsReceiver)
    }

    companion object {
        private const val TAG = "AdpActivity"
    }
}

