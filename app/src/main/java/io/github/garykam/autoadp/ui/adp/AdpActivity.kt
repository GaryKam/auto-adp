package io.github.garykam.autoadp.ui.adp

import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.Telephony
import android.util.Log
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import io.github.garykam.autoadp.R
import io.github.garykam.autoadp.receivers.SmsBroadcastReceiver
import io.github.garykam.autoadp.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore

class AdpActivity : ComponentActivity() {
    private var page = Page.WELCOME
    private lateinit var smsJob: Job
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(PreferencesUtil.TAG, "AdpActivity#onCreate")

        PreferencesUtil.initSharedPreferences(applicationContext)
        webView = WebView(this)

        setShowWhenLocked(true)
        setTurnScreenOn(true)

        registerReceiver(
            SmsBroadcastReceiver(this),
            IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
        )

        setContent {
            Log.d(PreferencesUtil.TAG, "AdpActivity#setContent")
            AdpScreen(webView = webView, onPageLoad = {
                when (page) {
                    Page.WELCOME -> {
                        Log.d(PreferencesUtil.TAG, "AdpActivity#visitLoginPage")
                        visitLoginPage()
                        page = Page.LOGIN
                    }

                    Page.LOGIN -> {
                        Log.d(PreferencesUtil.TAG, "AdpActivity#login")
                        login()
                        page = Page.HOME
                    }

                    Page.HOME -> {
                        Log.d(PreferencesUtil.TAG, "AdpActivity#clockOut")
                        clockOut()
                        page = Page.OTHER
                    }

                    Page.OTHER -> {}
                }
            })
        }
    }

    private fun visitLoginPage() {
        webView.runJavascript(
            "javascript: (function() {                                                         " +
                    "clickEvent = document.createEvent('HTMLEvents');                          " +
                    "clickEvent.initEvent('click', true, true);                                " +
                    "dropdown = document.querySelector('div[class=\"select__trigger\"]');      " +
                    "option = document.querySelector('span[data-value=\"anActiveEmployee\"]'); " +
                    "nextButton = document.querySelector('button[id=\"next\"]');               " +
                    "dropdown.dispatchEvent(clickEvent);                                       " +
                    "option.dispatchEvent(clickEvent);                                         " +
                    "nextButton.dispatchEvent(clickEvent);                                     " +
                    "}) ()                                                                     "
        )
    }

    private fun login() {
        CoroutineScope(Dispatchers.Main).launch {
            wait()
            webView.locateById("login-form_username")

            wait()
            webView.type(PreferencesUtil.getUsername())

            wait()
            webView.runJavascript(
                "javascript: (function() {                                       " +
                        "clickEvent = document.createEvent('HTMLEvents');        " +
                        "clickEvent.initEvent('click', true, true);              " +
                        "nextButton = document.getElementById('verifUseridBtn'); " +
                        "nextButton.dispatchEvent(clickEvent);                   " +
                        "}) ()"
            )

            webView.locateById("login-form_password")

            wait()
            webView.type(PreferencesUtil.getPassword())

            wait()
            webView.runJavascript(
                "javascript: (function() {                                  " +
                        "clickEvent = document.createEvent('HTMLEvents');   " +
                        "clickEvent.initEvent('click', true, true);         " +
                        "signInButton = document.getElementById('signBtn'); " +
                        "signInButton.dispatchEvent(clickEvent);            " +
                        "}) ()"
            )

            Log.d(PreferencesUtil.TAG, "AdpActivity#requestSmsCode")
            requestSmsCode()
        }
    }

    private fun requestSmsCode() {
        CoroutineScope(Dispatchers.Main).launch {
            webView.locateByClass("vdl-list-button vdl-default actionable")

            wait()
            webView.runJavascript(
                "javascript: (function() {                                                                         " +
                        "clickEvent = document.createEvent('HTMLEvents');                                          " +
                        "clickEvent.initEvent('click', true, true);                                                " +
                        "smsOption = document.getElementsByClassName('vdl-list-button vdl-default actionable')[0]; " +
                        "smsOption.dispatchEvent(clickEvent);                                                      " +
                        "}) ()"
            )
        }.also {
            smsJob = it
        }
    }

    fun enterSmsCode(code: String) {
        if (page != Page.HOME) {
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            webView.locateById("otpform")

            wait()
            webView.type(code)

            wait()
            webView.locateById("verifyOtpBtn")

            wait()
            webView.runJavascript(
                "javascript: (function() {                                       " +
                        "clickEvent = document.createEvent('HTMLEvents');        " +
                        "clickEvent.initEvent('click', true, true);              " +
                        "submitButton = document.getElementById('verifyOtpBtn'); " +
                        "submitButton.dispatchEvent(clickEvent);                 " +
                        "}) ()"
            )
        }
    }

    private fun clockOut() {
        if (smsJob.isActive) {
            Log.d(PreferencesUtil.TAG, "Job#cancel")
            smsJob.cancel()
        }

        CoroutineScope(Dispatchers.Main).launch {
            webView.locateById("chp-time-portlet-view-more-actions-btn")

            wait()
            webView.runJavascript(
                "javascript: (function() {                                                                  " +
                        "clickEvent = document.createEvent('HTMLEvents');                                   " +
                        "clickEvent.initEvent('click', true, true);                                         " +
                        "actionsButton = document.getElementById('chp-time-portlet-view-more-actions-btn'); " +
                        "actionsButton.scrollIntoView();                                                    " +
                        "actionsButton.dispatchEvent(clickEvent);                                           " +
                        "clockOutButton = document.getElementById('btn-id-more-actions-Clock Out');         " +
                        "clockOutButton.dispatchEvent(clickEvent);                                          " +
                        "}) ()"
            )

            MediaPlayer.create(this@AdpActivity, R.raw.success).start()
            NotificationUtil.clearNotification(this@AdpActivity)
        }
    }

    private suspend fun wait() {
        semaphore.acquire()
    }

    companion object {
        val semaphore = Semaphore(1)

        private enum class Page { WELCOME, LOGIN, HOME, OTHER }
    }
}

