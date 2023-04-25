package io.github.garykam.autoadp.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

object Utils {
    const val TAG = "AutoADP"
    private val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var encryptedSharedPreferences: SharedPreferences

    fun initSharedPreferences(context: Context) {
        sharedPreferences = context.getSharedPreferences("auto_adp_shared_pref", Context.MODE_PRIVATE)

        encryptedSharedPreferences = EncryptedSharedPreferences.create(
            "auto_adp_encrypted_shared_pref",
            masterKey,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun getUsername() = encryptedSharedPreferences.getString("username", "")!!

    fun getPassword() = encryptedSharedPreferences.getString("password", "")!!

    fun getTime() = sharedPreferences.getString("time", "")!!

    fun saveCredentials(username: String, password: String) {
        with(encryptedSharedPreferences.edit()) {
            putString("username", username)
            putString("password", password)
            apply()
        }
    }

    fun saveTime(time: String) {
        with(sharedPreferences.edit()) {
            putString("time", time)
            apply()
        }
    }
}