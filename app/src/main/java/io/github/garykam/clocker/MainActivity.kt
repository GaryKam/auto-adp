package io.github.garykam.clocker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Utils.setContext(applicationContext)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECEIVE_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(Utils.TAG, "MainActivity#requestPermission")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECEIVE_SMS), 69)
        }

        setContent {
            MainScreen(
                onClockOut = { startActivity(Intent(this, AdpActivity::class.java)) },
                onSave = { username, password ->
                    Utils.saveCredentials(username, password)
                    Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show()
                })
        }
    }
}