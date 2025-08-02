package com.chiu.renovadoproyecto1.features.login.presentation.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chiu.renovadoproyecto1.features.login.domain.usecase.LoginUseCase
import com.chiu.renovadoproyecto1.core.hardware.domain.Biometric.AuthenticateBiometricUseCase

class LoginViewModelFactory(
    private val loginUseCase: LoginUseCase,
    private val biometricUseCase: AuthenticateBiometricUseCase,
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(loginUseCase, biometricUseCase, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}