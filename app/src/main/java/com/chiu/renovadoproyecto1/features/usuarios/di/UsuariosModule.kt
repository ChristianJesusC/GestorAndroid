@file:Suppress("UNCHECKED_CAST")

package com.chiu.renovadoproyecto1.features.usuarios.di

import com.chiu.renovadoproyecto1.core.http.RetrofitHelper
import com.chiu.renovadoproyecto1.features.login.di.AppModule
import com.chiu.renovadoproyecto1.features.usuarios.data.datasource.remote.UsuariosService
import com.chiu.renovadoproyecto1.features.usuarios.data.repository.UsuariosRepositoryImpl
import com.chiu.renovadoproyecto1.features.usuarios.domain.repository.UsuariosRepository
import com.chiu.renovadoproyecto1.features.usuarios.domain.usecase.GetUsuariosUseCase
import com.chiu.renovadoproyecto1.features.usuarios.presentation.viewmodel.UsuariosViewModelFactory

object UsuariosModule {

    private val usuariosService: UsuariosService by lazy {
        RetrofitHelper.getService(UsuariosService::class.java)
    }

    private val usuariosRepository: UsuariosRepository by lazy {
        UsuariosRepositoryImpl(usuariosService)
    }

    private val getUsuariosUseCase: GetUsuariosUseCase by lazy {
        GetUsuariosUseCase(usuariosRepository, AppModule.getTokenRepository)
    }

    fun getUsuariosViewModelFactory(): UsuariosViewModelFactory {
        return UsuariosViewModelFactory(
            getUsuariosUseCase = getUsuariosUseCase,
            tokenRepository = AppModule.getTokenRepository
        )
    }
}