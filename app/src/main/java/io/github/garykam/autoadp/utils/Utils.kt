package io.github.garykam.autoadp.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

object Utils {
    const val TAG = "AutoADP"
    private val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    private lateinit var sharedPreferences: SharedPreferences

    fun saveCredentials(username: String, password: String) {
        sharedPreferences.edit().run {
            putString("username", username)
            putString("password", password)
            apply()
        }
    }

    fun getUsername() = sharedPreferences.getString("username", "")!!

    fun getPassword() = sharedPreferences.getString("password", "")!!

    fun initSharedPreferences(context: Context) {
        sharedPreferences = EncryptedSharedPreferences.create(
            "secret_shared_prefs",
            masterKey,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}