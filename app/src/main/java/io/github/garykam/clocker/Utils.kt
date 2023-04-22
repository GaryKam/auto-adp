package io.github.garykam.clocker

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.view.KeyCharacterMap

object Utils {
    private val keyMap = KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD)

    fun runJavascript(javascript: String) {
        Handler(Looper.getMainLooper()).postDelayed({
            MainActivity.webView.get()!!.loadUrl(javascript)
        }, 500L)
    }

    fun waitUntil(javascript: String) {
        Handler(Looper.getMainLooper()).post {
            MainActivity.webView.get()!!.evaluateJavascript(javascript) { value ->
                if (value.equals("null")) {
                    Thread.sleep(250L)
                    waitUntil(javascript)
                } else {
                    MainActivity.semaphore.release()
                }
            }
        }
    }

    fun type(text: String) {
        keyMap.getEvents(text.toCharArray()).forEach {
            MainActivity.webView.get()!!.dispatchKeyEvent(it)
        }

        MainActivity.semaphore.release()
    }

    fun playSound(context: Context) {
        MediaPlayer.create(context, R.raw.success).start()
    }
}