package io.github.garykam.autoadp.ui.adp

import android.content.Context
import android.media.MediaPlayer
import android.webkit.JavascriptInterface
import io.github.garykam.autoadp.R
import io.github.garykam.autoadp.utils.NotificationUtil

class AdpInterface(private val context: Context) {
    @Suppress("unused")
    @JavascriptInterface
    fun onClockOut() {
        MediaPlayer.create(context, R.raw.success).start()
        NotificationUtil.clearNotification(context)
    }
}