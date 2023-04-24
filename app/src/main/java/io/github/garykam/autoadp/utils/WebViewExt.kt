package io.github.garykam.autoadp.utils

import android.os.Handler
import android.os.Looper
import android.view.KeyCharacterMap
import android.webkit.WebView
import io.github.garykam.autoadp.AdpActivity

private val keyMap = KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD)

fun WebView.runJavascript(javascript: String) {
    Handler(Looper.getMainLooper()).postDelayed({
        loadUrl(javascript)
    }, 500L)
}

fun WebView.waitUntil(javascript: String) {
    Handler(Looper.getMainLooper()).post {
        evaluateJavascript(javascript) { value ->
            if (value.equals("null")) {
                Thread.sleep(250L)
                waitUntil(javascript)
            } else {
                AdpActivity.semaphore.release()
            }
        }
    }
}

fun WebView.type(text: String) {
    keyMap.getEvents(text.toCharArray()).forEach {
        dispatchKeyEvent(it)
    }

    AdpActivity.semaphore.release()
}

