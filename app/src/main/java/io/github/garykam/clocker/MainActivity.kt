package io.github.garykam.clocker

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import java.lang.ref.WeakReference

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate")
        setShowWhenLocked(true)
        setTurnScreenOn(true)

        setContent {
            Log.d(TAG, "setContent")
            AdpView()
        }
    }

    @Composable
    @SuppressLint("SetJavaScriptEnabled")
    private fun AdpView() {
        AndroidView(factory = {
            WebView(this).apply {
                var page = Page.WELCOME

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
                                        Log.d(TAG, "visitLoginPage")
                                        visitLoginPage()
                                        page = Page.SIGN_IN
                                    }

                                    Page.SIGN_IN -> {
                                        Log.d(TAG, "loginWithCredentials")
                                        loginWithCredentials()
                                        page = Page.HOME
                                    }

                                    Page.HOME -> {
                                        Log.d(TAG, "clockOut")
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
                webView = WeakReference(it)
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

    private fun loginWithCredentials() {
        CoroutineScope(Dispatchers.Main).launch {
            semaphore.acquire()
            Utils.waitUntil("document.getElementById('login-form_username')")

            semaphore.acquire()
            Utils.type(USERNAME)

            semaphore.acquire()
            webView.get()!!.requestFocus()
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
            Utils.type(PASSWORD)

            semaphore.acquire()
            Utils.runJavascript(
                "javascript: (function() {                                  " +
                        "clickEvent = document.createEvent('HTMLEvents');   " +
                        "clickEvent.initEvent('click', true, true);         " +
                        "signInButton = document.getElementById('signBtn'); " +
                        "signInButton.dispatchEvent(clickEvent);            " +
                        "}) ()"
            )
        }
    }

    private fun clockOut() {
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
                        "clockOutButton.dispatchEvent(clickEvent);                                          " +
                        "}) ()"
            )

            Utils.playSound(applicationContext)
            //finish()
        }
    }

    companion object {
        lateinit var webView: WeakReference<WebView>
            private set
        val semaphore = Semaphore(1)
        private const val TAG = "Clocker"
        private const val ADP_URL = "https://login.adp.com/welcome"
        private const val USERNAME = "username"
        private const val PASSWORD = "password"

        private enum class Page { WELCOME, SIGN_IN, HOME, OTHER }
    }
}
