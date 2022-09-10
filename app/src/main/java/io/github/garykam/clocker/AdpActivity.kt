package io.github.garykam.clocker

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

class AdpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AdpView()
        }
    }

    @Composable
    @SuppressLint("SetJavaScriptEnabled")
    private fun AdpView() {
        AndroidView(factory = {
            WebView(this).apply {
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        loginToAdp(view)
                    }
                }

                loadUrl("https://login.adp.com/welcome")
                settings.javaScriptEnabled = true
            }
        }, modifier = Modifier.fillMaxSize())
    }

    private fun loginToAdp(webView: WebView?) {
        webView?.loadUrl(
            "javascript:(function() {                                                                " +
                    "clickEvent = document.createEvent('HTMLEvents');                                " +
                    "clickEvent.initEvent('click', true, true);                                      " +
                    "dropdown = document.querySelectorAll('div[class=\"select__trigger\"]')[0];      " +
                    "option = document.querySelectorAll('span[data-value=\"anActiveEmployee\"]')[0]; " +
                    "nextButton = document.querySelector('button[id=\"next\"]');                     " +
                    "dropdown.dispatchEvent(clickEvent);                                             " +
                    "option.dispatchEvent(clickEvent);                                               " +
                    "nextButton.dispatchEvent(clickEvent);                                           " +
                    "}) ()                                                                           "
        )
    }
}