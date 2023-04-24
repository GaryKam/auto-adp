package io.github.garykam.clocker

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log

class SmsBroadcastReceiver(private val adpActivity: AdpActivity) : BroadcastReceiver() {
    private val regex = Regex("ADP:.+(?<code>\\d{6}).+")

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(Utils.TAG, "SmsBroadcastReceiver#onReceive")
        if (resultCode == Activity.RESULT_OK) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)

            for (message in messages) {
                val code = regex.matchEntire(message.messageBody!!)?.groups?.get("code")

                if (code != null) {
                    Log.d(Utils.TAG, "SmsBroadcastReceiver#enterSmsCode")
                    adpActivity.enterSmsCode(code.value)
                    break
                }
            }
        }
    }
}