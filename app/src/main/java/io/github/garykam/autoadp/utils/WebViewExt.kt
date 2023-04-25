package io.github.garykam.autoadp.utils

import android.os.Handler
import android.os.Looper
import android.view.KeyCharacterMap
import android.webkit.WebView
import io.github.garykam.autoadp.ui.AdpActivity

private val keyMap = KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD)

fun WebView.runJavascript(javascript: String) {
    Handler(Looper.getMainLooper()).postDelayed({
        loadUrl(javascript)
    }, 500L)
}

fun WebView.locateById(id: String) {
    Handler(Looper.getMainLooper()).post {
        evaluateJavascript("document.getElementById('$id')") { value ->
            if (value.equals("null")) {
                Thread.sleep(250L)
                locateById(id)
            } else {
                AdpActivity.semaphore.release()
            }
        }
    }
}

fun WebView.locateByClass(className: String) {
    Handler(Looper.getMainLooper()).post {
        evaluateJavascript("document.getElementsByClassName('$className')[0]") { value ->
            if (value.equals("null")) {
                Thread.sleep(250L)
                locateByClass(className)
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