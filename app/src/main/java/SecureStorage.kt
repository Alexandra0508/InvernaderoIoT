package com.example.myapplication

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecureStorage(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_iot_session",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveCredentials(user: String, pass: String) {
        sharedPreferences.edit()
            .putString("KEY_USER", user)
            .putString("KEY_PASS", pass)
            .apply()
    }

    fun getUser(): String? {
        return sharedPreferences.getString("KEY_USER", null)
    }

    fun clearSession() {
        sharedPreferences.edit().clear().apply()
    }
}