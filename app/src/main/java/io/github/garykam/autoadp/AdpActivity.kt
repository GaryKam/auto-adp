package io.github.garykam.clocker

import android.annotation.SuppressLint
import android.content.IntentFilter
import android.os.Bundle
import android.provider.Telephony
import android.util.Log
import android.view.WindowManager
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore

class AdpActivity : ComponentActivity() {
    private lateinit var job: Job
    private var page = Page.WELCOME

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(Utils.TAG, "AdpActivity#onCreate")

        Utils.setContext(applicationContext)

        setShowWhenLocked(true)
        setTurnScreenOn(true)

        registerReceiver(
            SmsBroadcastReceiver(this),
            IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
        )

        setContent {
            Log.d(Utils.TAG, "AdpActivity#setContent")
            AdpView()
        }
    }

    @Composable
    @SuppressLint("SetJavaScriptEnabled")
    private fun AdpView() {
        AndroidView(factory = {
            WebView(this).apply {
                loadUrl(ADP_URL)
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true

                CookieManager.getInstance().removeAllCookies(null)

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(webView: WebView, url: String) {
                        webView.postVisualStateCallback(0L, object : WebView.VisualStateCallback() {
                            override fun onComplete(requestId: Long) {
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
                            }
                        })
                    }
                }
            }.also {
                Utils.setWebView(it)
            }
        }, modifier = Modifier.fillMaxSize())
    }

    private fun visitLoginPage() {
        Utils.runJavascript(
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
            Utils.waitUntil("document.getElementById('login-form_username')")

            semaphore.acquire()
            Utils.type(Utils.getUsername())

            semaphore.acquire()
            Utils.runJavascript(
                "javascript: (function() {                                       " +
                        "clickEvent = document.createEvent('HTMLEvents');        " +
                        "clickEvent.initEvent('click', true, true);              " +
                        "nextButton = document.getElementById('verifUseridBtn'); " +
                        "nextButton.dispatchEvent(clickEvent);                   " +
                        "}) ()"
            )

            Utils.waitUntil("document.getElementById('login-form_password')")

            semaphore.acquire()
            Utils.type(Utils.getPassword())

            semaphore.acquire()
            Utils.runJavascript(
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
            Utils.waitUntil("document.getElementsByClassName('vdl-list-button vdl-default actionable')[0]")

            semaphore.acquire()
            Utils.runJavascript(
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
            Utils.waitUntil("document.getElementById('otpform')")

            semaphore.acquire()
            Utils.type(code)

            semaphore.acquire()
            Utils.waitUntil("document.getElementById('verifyOtpBtn')")

            semaphore.acquire()
            Utils.runJavascript(
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
            Utils.waitUntil("document.getElementById('chp-time-portlet-view-more-actions-btn')")

            semaphore.acquire()
            Utils.runJavascript(
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

            Utils.playSound()
            //finish()
        }
    }

    companion object {
        val semaphore = Semaphore(1)
        private const val ADP_URL = "https://login.adp.com/welcome"

        private enum class Page { WELCOME, LOGIN, HOME, OTHER }
    }
}
