package com.chiu.renovadoproyecto1.features.usuarios.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chiu.renovadoproyecto1.features.login.domain.repository.TokenRepository
import com.chiu.renovadoproyecto1.features.usuarios.domain.usecase.GetUsuariosUseCase

class UsuariosViewModelFactory(
    private val getUsuariosUseCase: GetUsuariosUseCase,
    private val tokenRepository: TokenRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsuariosViewModel::class.java)) {
            return UsuariosViewModel(getUsuariosUseCase, tokenRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}