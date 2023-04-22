package io.github.garykam.clocker

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.view.KeyCharacterMap
import android.webkit.WebView
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import java.lang.ref.WeakReference


object Utils {
    const val TAG = "ClockerApp"
    private val keyMap = KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD)
    private val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    private val sharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            "secret_shared_prefs",
            masterKey,
            context.get()!!,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    private lateinit var context: WeakReference<Context>
    private lateinit var webView: WeakReference<WebView>

    fun runJavascript(javascript: String) {
        Handler(Looper.getMainLooper()).postDelayed({
            webView.get()!!.loadUrl(javascript)
        }, 500L)
    }

    fun waitUntil(javascript: String) {
        Handler(Looper.getMainLooper()).post {
            webView.get()!!.evaluateJavascript(javascript) { value ->
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
            webView.get()!!.dispatchKeyEvent(it)
        }

        AdpActivity.semaphore.release()
    }

    fun playSound() {
        MediaPlayer.create(context.get(), R.raw.success).start()
    }

    fun saveCredentials(username: String, password: String) {
        sharedPreferences.edit().run {
            putString("username", username)
            putString("password", password)
            apply()
        }
    }

    fun getUsername() = sharedPreferences.getString("username", "")!!

    fun getPassword() = sharedPreferences.getString("password", "")!!

    fun setContext(_context: Context) {
        context = WeakReference(_context)
    }

    fun setWebView(_webView: WebView) {
        webView = WeakReference(_webView)
    }
}