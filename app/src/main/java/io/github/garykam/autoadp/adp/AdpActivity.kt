package io.github.garykam.autoadp.ui.adp

import android.content.IntentFilter
import android.os.Bundle
import android.provider.Telephony
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import io.github.garykam.autoadp.receivers.SmsBroadcastReceiver
import io.github.garykam.autoadp.utils.PreferencesUtil

class AdpActivity : ComponentActivity() {
    private val smsBroadcastReceiver = SmsBroadcastReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")

        PreferencesUtil.initSharedPreferences(applicationContext)

        setShowWhenLocked(true)
        setTurnScreenOn(true)

        setContent {
            Log.d(TAG, "setContent")
            AdpScreen()
        }
    }

    override fun onResume() {
        super.onResume()

        registerReceiver(smsBroadcastReceiver, IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION))
    }

    override fun onPause() {
        super.onPause()

        unregisterReceiver(smsBroadcastReceiver)
    }

    companion object {
        private const val TAG = "AdpActivity"
    }
}

