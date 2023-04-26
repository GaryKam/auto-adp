package io.github.garykam.autoadp.adp

import android.annotation.SuppressLint
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

var page = Page.WELCOME

enum class Page { WELCOME, LOGIN, HOME, IGNORE }

private const val ADP_LOGIN_URL = "https://login.adp.com/welcome"

@Composable
@SuppressLint("SetJavaScriptEnabled")
fun AdpScreen() {
    val webView = WebView(LocalContext.current)
    AndroidView(factory = {
        webView.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true

            CookieManager.getInstance().removeAllCookies(null)
            addJavascriptInterface(AdpInterface(it), "Adp")
            loadUrl(ADP_LOGIN_URL)

            webViewClient = object : WebViewClient() {
                override fun onPageFinished(webView: WebView, url: String) {
                    webView.postVisualStateCallback(0L, object : WebView.VisualStateCallback() {
                        override fun onComplete(requestId: Long) {
                            when (page) {
                                Page.WELCOME -> AdpHelper.visitLoginPage(webView)
                                Page.LOGIN -> AdpHelper.login(webView)
                                Page.HOME -> AdpHelper.clockOut(webView)
                                else -> {}
                            }
                        }
                    })
                }
            }
        }
    }, modifier = Modifier.fillMaxSize())
}