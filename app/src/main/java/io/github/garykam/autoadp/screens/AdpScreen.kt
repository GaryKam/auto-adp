package io.github.garykam.autoadp.screens

import android.annotation.SuppressLint
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
@SuppressLint("SetJavaScriptEnabled")
fun AdpScreen(webView: WebView, onPageLoad: () -> Unit) {
    AndroidView(factory = {
        webView.apply {
            loadUrl("https://login.adp.com/welcome")
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true

            CookieManager.getInstance().removeAllCookies(null)

            webViewClient = object : WebViewClient() {
                override fun onPageFinished(webView: WebView, url: String) {
                    webView.postVisualStateCallback(0L, object : WebView.VisualStateCallback() {
                        override fun onComplete(requestId: Long) {
                            onPageLoad()
                        }
                    })
                }
            }
        }
    }, modifier = Modifier.fillMaxSize())
}