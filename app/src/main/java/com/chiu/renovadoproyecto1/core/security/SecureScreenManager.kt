package com.chiu.renovadoproyecto1.core.security

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SecureScreenManager {
    private val _secureScreenCount = MutableStateFlow(0)
    private val _isSecureScreen = MutableStateFlow(false)
    val isSecureScreen: StateFlow<Boolean> = _isSecureScreen.asStateFlow()

    private var count = 0
        set(value) {
            field = value.coerceAtLeast(0)
            _secureScreenCount.value = field
            _isSecureScreen.value = field > 0
            Log.d("SecureScreenManager", "📊 Pantallas seguras activas: $field, Bloqueado: ${field > 0}")
        }

    fun enterSecureScreen() {
        count++
        Log.d("SecureScreenManager", "🔒 Entrando a pantalla segura (total: $count)")
    }

    fun exitSecureScreen() {
        count--
        Log.d("SecureScreenManager", "🔓 Saliendo de pantalla segura (total: $count)")
    }

    fun reset() {
        count = 0
        Log.d("SecureScreenManager", "🔄 Reset contador de pantallas seguras")
    }
}