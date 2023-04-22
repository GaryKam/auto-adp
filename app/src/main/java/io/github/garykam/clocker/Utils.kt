package io.github.garykam.clocker

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.view.KeyCharacterMap

object Utils {
    const val TAG = "ClockerApp"
    private val keyMap = KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD)

    fun runJavascript(javascript: String) {
        Handler(Looper.getMainLooper()).postDelayed({
            AdpActivity.webView.get()!!.loadUrl(javascript)
        }, 500L)
    }

    fun waitUntil(javascript: String) {
        Handler(Looper.getMainLooper()).post {
            AdpActivity.webView.get()!!.evaluateJavascript(javascript) { value ->
                if (value.equals("null")) {
                    Thread.sleep(250L)
                    waitUntil(javascript)
                } else {
                    AdpActivity.semaphore.release()
                }
            }
        }
    }

    fun type(text: String) {
        keyMap.getEvents(text.toCharArray()).forEach {
            AdpActivity.webView.get()!!.dispatchKeyEvent(it)
        }

        AdpActivity.semaphore.release()
    }

    fun playSound(context: Context) {
        MediaPlayer.create(context, R.raw.success).start()
    }
}