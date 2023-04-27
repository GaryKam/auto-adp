package io.github.garykam.autoadp.receivers

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import io.github.garykam.autoadp.adp.AdpHelper

class SmsReceiver : BroadcastReceiver() {
    private val regex = Regex("ADP:.+(?<code>\\d{6}).+")

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive")
        if (resultCode == Activity.RESULT_OK) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)

            for (message in messages) {
                val code = regex.matchEntire(message.messageBody!!)?.groups?.get("code")

                if (code != null) {
                    Log.d(TAG, "enterSmsCode")
                    AdpHelper.enterSmsCode(code.value)
                    break
                }
            }
        }
    }

    companion object {
        private const val TAG = "SmsBroadcastReceiver"
    }
}