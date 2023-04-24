package io.github.garykam.autoadp

import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.Telephony
import android.util.Log
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import io.github.garykam.autoadp.screens.AdpScreen
import io.github.garykam.autoadp.utils.Utils
import io.github.garykam.autoadp.utils.runJavascript
import io.github.garykam.autoadp.utils.type
import io.github.garykam.autoadp.utils.waitUntil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore

class AdpActivity : ComponentActivity() {
    private var page = Page.WELCOME
    private lateinit var job: Job
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(Utils.TAG, "AdpActivity#onCreate")

        Utils.initSharedPreferences(applicationContext)
        webView = WebView(this)

        setShowWhenLocked(true)
        setTurnScreenOn(true)

        registerReceiver(
            SmsBroadcastReceiver(this),
            IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
        )

        setContent {
            Log.d(Utils.TAG, "AdpActivity#setContent")
            AdpScreen(webView = webView, onPageLoad = {
                when (page) {
                    Page.WELCOME -> {
                        Log.d(Utils.TAG, "AdpActivity#visitLoginPage")
                        visitLoginPage()
                        page = Page.LOGIN
                    }

                    Page.LOGIN -> {
                        Log.d(Utils.TAG, "AdpActivity#login")
                        login()
                        page = Page.HOME
                    }

                    Page.HOME -> {
                        Log.d(Utils.TAG, "AdpActivity#clockOut")
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
            semaphore.acquire()
            webView.waitUntil("document.getElementById('login-form_username')")

            semaphore.acquire()
            webView.type(Utils.getUsername())

            semaphore.acquire()
            webView.runJavascript(
                "javascript: (function() {                                       " +
                        "clickEvent = document.createEvent('HTMLEvents');        " +
                        "clickEvent.initEvent('click', true, true);              " +
                        "nextButton = document.getElementById('verifUseridBtn'); " +
                        "nextButton.dispatchEvent(clickEvent);                   " +
                        "}) ()"
            )

            webView.waitUntil("document.getElementById('login-form_password')")

            semaphore.acquire()
            webView.type(Utils.getPassword())

            semaphore.acquire()
            webView.runJavascript(
                "javascript: (function() {                                  " +
                        "clickEvent = document.createEvent('HTMLEvents');   " +
                        "clickEvent.initEvent('click', true, true);         " +
                        "signInButton = document.getElementById('signBtn'); " +
                        "signInButton.dispatchEvent(clickEvent);            " +
                        "}) ()"
            )

            Log.d(Utils.TAG, "AdpActivity#requestSmsCode")
            requestSmsCode()
        }
    }

    private fun requestSmsCode() {
        CoroutineScope(Dispatchers.Main).launch {
            webView.waitUntil("document.getElementsByClassName('vdl-list-button vdl-default actionable')[0]")

            semaphore.acquire()
            webView.runJavascript(
                "javascript: (function() {                                                                         " +
                        "clickEvent = document.createEvent('HTMLEvents');                                          " +
                        "clickEvent.initEvent('click', true, true);                                                " +
                        "smsOption = document.getElementsByClassName('vdl-list-button vdl-default actionable')[0]; " +
                        "smsOption.dispatchEvent(clickEvent);                                                      " +
                        "}) ()"
            )
        }.also {
            job = it
        }
    }

    fun enterSmsCode(code: String) {
        if (page != Page.HOME) {
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            webView.waitUntil("document.getElementById('otpform')")

            semaphore.acquire()
            webView.type(code)

            semaphore.acquire()
            webView.waitUntil("document.getElementById('verifyOtpBtn')")

            semaphore.acquire()
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
        if (job.isActive) {
            Log.d(Utils.TAG, "Job#cancel")
            job.cancel()
        }

        CoroutineScope(Dispatchers.Main).launch {
            webView.waitUntil("document.getElementById('chp-time-portlet-view-more-actions-btn')")

            semaphore.acquire()
            webView.runJavascript(
                "javascript: (function() {                                                                  " +
                        "clickEvent = document.createEvent('HTMLEvents');                                   " +
                        "clickEvent.initEvent('click', true, true);                                         " +
                        "actionsButton = document.getElementById('chp-time-portlet-view-more-actions-btn'); " +
                        "actionsButton.scrollIntoView();                                                    " +
                        "actionsButton.dispatchEvent(clickEvent);                                           " +
                        "clockOutButton = document.getElementById('btn-id-more-actions-Clock Out');         " +
                        //"clockOutButton.dispatchEvent(clickEvent);                                          " +
                        "}) ()"
            )

            MediaPlayer.create(this@AdpActivity, R.raw.success).start()
            //finish()
        }
    }

    companion object {
        val semaphore = Semaphore(1)

        private enum class Page { WELCOME, LOGIN, HOME, OTHER }
    }
}
