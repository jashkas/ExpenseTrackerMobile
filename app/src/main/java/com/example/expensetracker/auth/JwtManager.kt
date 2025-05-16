package com.example.expensetracker.auth

import com.auth0.android.jwt.JWT
import com.example.expensetracker.data.local.SecurePrefs
import java.util.Date
import javax.inject.Inject

class JwtManager @Inject constructor(private val securePrefs: SecurePrefs) {
    fun saveToken(token: String) {
        securePrefs.saveToken(token)
        val jwt = JWT(token)
        securePrefs.saveUserId(jwt.subject ?: "")
    }

    fun getToken(): String? = securePrefs.getToken()

    fun isTokenValid(): Boolean {
        val token = securePrefs.getToken() ?: return false
        return try {
            val jwt = JWT(token)
            !jwt.isExpired(Date().time / 1000)
        } catch (e: Exception) {
            false
        }
    }

    fun getUserId(): String? = securePrefs.getUserId()

    fun clearToken() {
        securePrefs.clearToken()
    }
}