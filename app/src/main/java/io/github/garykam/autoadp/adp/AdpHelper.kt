package io.github.garykam.autoadp.adp

import android.webkit.WebView
import io.github.garykam.autoadp.utils.PreferencesUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import java.lang.ref.WeakReference

private lateinit var smsJob: Job
private lateinit var webViewRef: WeakReference<WebView>

object AdpHelper {
    val semaphore = Semaphore(1)

    fun visitLoginPage(webView: WebView) {
        webView.runJavascript(
            """javascript: (function() {
                clickEvent = document.createEvent('HTMLEvents');
                clickEvent.initEvent('click', true, true);
                dropdown = document.querySelector('div[class=\"select__trigger\"]');
                option = document.querySelector('span[data-value=\"anActiveEmployee\"]');
                nextButton = document.querySelector('button[id=\"next\"]');
                dropdown.dispatchEvent(clickEvent);
                option.dispatchEvent(clickEvent);
                nextButton.dispatchEvent(clickEvent);
                }) ()"""
        )

        page = Page.LOGIN
    }

    fun login(webView: WebView) {
        CoroutineScope(Dispatchers.Main).launch {
            wait()
            webView.locateById("login-form_username")

            wait()
            webView.type(PreferencesUtil.getUsername())

            wait()
            webView.runJavascript(
                """javascript: (function() {
                        clickEvent = document.createEvent('HTMLEvents');
                        clickEvent.initEvent('click', true, true);
                        nextButton = document.getElementById('verifUseridBtn');
                        nextButton.dispatchEvent(clickEvent);
                        }) ()"""
            )

            webView.locateById("login-form_password")

            wait()
            webView.type(PreferencesUtil.getPassword())

            wait()
            webView.runJavascript(
                """javascript: (function() {
                        clickEvent = document.createEvent('HTMLEvents');
                        clickEvent.initEvent('click', true, true);
                        signInButton = document.getElementById('signBtn');
                        signInButton.dispatchEvent(clickEvent);
                        }) ()"""
            )

            page = Page.HOME
            requestSmsCode(webView)
        }
    }

    private fun requestSmsCode(webView: WebView) {
        CoroutineScope(Dispatchers.Main).launch {
            webView.locateByClass("vdl-list-button vdl-default actionable")

            wait()
            webView.runJavascript(
                """javascript: (function() {
                        clickEvent = document.createEvent('HTMLEvents');
                        clickEvent.initEvent('click', true, true);
                        smsOption = document.getElementsByClassName('vdl-list-button vdl-default actionable')[0];
                        smsOption.dispatchEvent(clickEvent);
                        }) ()"""
            )
        }.also {
            smsJob = it
            webViewRef = WeakReference(webView)
        }
    }

    fun enterSmsCode(code: String) {
        if (page != Page.HOME) {
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            webViewRef.get()!!.locateById("otpform")

            wait()
            webViewRef.get()!!.type(code)

            wait()
            webViewRef.get()!!.locateById("verifyOtpBtn")

            wait()
            webViewRef.get()!!.runJavascript(
                """javascript: (function() {
                        clickEvent = document.createEvent('HTMLEvents');
                        clickEvent.initEvent('click', true, true); 
                        submitButton = document.getElementById('verifyOtpBtn');
                        submitButton.dispatchEvent(clickEvent);
                        }) ()"""
            )
        }
    }

    fun clockOut(webView: WebView) {
        webViewRef.clear()
        if (smsJob.isActive) {
            smsJob.cancel()
        }

        CoroutineScope(Dispatchers.Main).launch {
            webView.locateById("chp-time-portlet-view-more-actions-btn")

            wait()
            webView.runJavascript(
                """javascript: (function() {
                        clickEvent = document.createEvent('HTMLEvents');
                        clickEvent.initEvent('click', true, true);
                        actionsButton = document.getElementById('chp-time-portlet-view-more-actions-btn');
                        actionsButton.scrollIntoView();
                        actionsButton.dispatchEvent(clickEvent);
                        clockOutButton = document.getElementById('btn-id-more-actions-Clock Out');
                        clockOutButton.setAttribute('onclick', 'Adp.onClockOut()');
                        clockOutButton.dispatchEvent(clickEvent);
                        }) ()"""
            )

            page = Page.IGNORE
        }
    }

    private suspend fun wait() {
        semaphore.acquire()
    }
}