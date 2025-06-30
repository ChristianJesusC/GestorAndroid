package com.chiu.renovadoproyecto1.features.login.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chiu.renovadoproyecto1.features.login.domain.usecase.LoginUseCase

class LoginViewModelFactory (
    private val loginUseCase: LoginUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(loginUseCase) as T
    }
}