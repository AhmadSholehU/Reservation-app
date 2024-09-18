package com.overdevx.reservationapp.utils

import android.content.SharedPreferences
import javax.inject.Inject

class TokenProvider @Inject constructor(
    private val sharedPreferences: SharedPreferences // or other secure storage
) {
    fun getToken(): String? {
        return sharedPreferences.getString("auth_token", "") ?: ""
    }

    fun saveToken(token: String) {
        sharedPreferences.edit().putString("auth_token", token).apply()
    }

    fun clearToken() {
        sharedPreferences.edit().remove("auth_token").apply()
    }
}
