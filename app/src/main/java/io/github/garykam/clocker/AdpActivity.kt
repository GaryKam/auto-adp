package io.github.garykam.clocker

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyCharacterMap
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

class AdpActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate")
        setShowWhenLocked(true)
        setTurnScreenOn(true)

        ClockHelper.loadSchedule(this, mainViewModel)
        ClockHelper.handleClockOption(this, mainViewModel)

        setContent {
            AdpView()
        }
    }

    @Composable
    @SuppressLint("SetJavaScriptEnabled")
    private fun AdpView() {
        Log.d(TAG, "AdpView")
        AndroidView(factory = {
            WebView(this).apply {
                var page = Page.WELCOME

                loadUrl(ADP_URL)
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        when (page) {
                            Page.WELCOME -> {
                                visitLoginPage(view)
                                page = Page.SIGN_IN
                            }

                            Page.SIGN_IN -> {
                                loginWithUserCredentials(view)
                                page = Page.HOME
                            }

                            Page.HOME -> {
                                clockOut(view)
                                page = Page.OTHER
                            }

                            Page.OTHER -> {}
                        }
                    }
                }
            }
        }, modifier = Modifier.fillMaxSize())
    }

    private fun visitLoginPage(webView: WebView?) {
        Log.d(TAG, "visitLoginPage")
        webView?.loadUrl(
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

    private fun loginWithUserCredentials(webView: WebView?) {
        Log.d(TAG, "loginWithUserCredentials")
        val username = USERNAME
        val password = PASSWORD
        val keyMap = KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD)

        Handler(Looper.getMainLooper()).postDelayed({
            keyMap.getEvents(username.toCharArray()).forEach {
                webView?.dispatchKeyEvent(it)
            }

            webView?.requestFocus()
            webView?.loadUrl(
                "javascript: (function() {                                       " +
                        "clickEvent = document.createEvent('HTMLEvents');        " +
                        "clickEvent.initEvent('click', true, true);              " +
                        "nextButton = document.getElementById('verifUseridBtn'); " +
                        "nextButton.dispatchEvent(clickEvent);                   " +
                        "}) ()"
            )
        }, DELAY)

        Handler(Looper.getMainLooper()).postDelayed({
            keyMap.getEvents(password.toCharArray()).forEach {
                webView?.dispatchKeyEvent(it)
            }
        }, DELAY * 6)

        Handler(Looper.getMainLooper()).postDelayed({
            webView?.loadUrl(
                "javascript: (function() {                                  " +
                        "clickEvent = document.createEvent('HTMLEvents');   " +
                        "clickEvent.initEvent('click', true, true);         " +
                        "signInButton = document.getElementById('signBtn'); " +
                        "signInButton.dispatchEvent(clickEvent);            " +
                        "}) ()"
            )
        }, DELAY * 12)
    }

    private fun clockOut(webView: WebView?) {
        Log.d(TAG, "clockOut")
        Handler(Looper.getMainLooper()).postDelayed({
            webView?.loadUrl(
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
        }, DELAY * 20)
    }

    companion object {
        private const val TAG = "AdpActivity"
        private const val ADP_URL = "https://login.adp.com/welcome"
        private const val USERNAME = "username"
        private const val PASSWORD = "password"
        private const val DELAY = 500L

        private enum class Page { WELCOME, SIGN_IN, HOME, OTHER }
    }
}