package com.chiu.renovadoproyecto1.core.jwt

import android.util.Base64
import android.util.Log
import org.json.JSONObject

object JwtHelper {

    fun isTokenExpired(token: String): Boolean {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) {
                Log.e("JwtHelper", "Token JWT inválido")
                return true
            }

            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
            val jsonObject = JSONObject(payload)

            val exp = jsonObject.optLong("exp", 0)
            if (exp == 0L) {
                Log.e("JwtHelper", "Token sin fecha de expiración")
                return true
            }

            val currentTime = System.currentTimeMillis() / 1000
            val isExpired = currentTime >= exp

            Log.d("JwtHelper", "Token expira en: $exp, tiempo actual: $currentTime, expirado: $isExpired")

            return isExpired
        } catch (e: Exception) {
            Log.e("JwtHelper", "Error verificando token: ${e.message}")
            return true
        }
    }

    fun getTimeUntilExpiration(token: String): Long {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return 0

            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
            val jsonObject = JSONObject(payload)
            val exp = jsonObject.optLong("exp", 0)

            val currentTime = System.currentTimeMillis() / 1000
            return maxOf(0, exp - currentTime)
        } catch (e: Exception) {
            0
        }
    }
}