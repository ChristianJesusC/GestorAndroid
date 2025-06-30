@file:Suppress("UNCHECKED_CAST")

package com.chiu.renovadoproyecto1.features.juegos.di

import com.chiu.renovadoproyecto1.core.http.RetrofitHelper
import com.chiu.renovadoproyecto1.features.juegos.data.datasource.remote.JuegosService
import com.chiu.renovadoproyecto1.features.juegos.data.repository.JuegosRepositoryImpl
import com.chiu.renovadoproyecto1.features.juegos.domain.repository.JuegosRepository
import com.chiu.renovadoproyecto1.features.juegos.domain.usecase.CreateJuegoUseCase
import com.chiu.renovadoproyecto1.features.juegos.domain.usecase.DeleteJuegoUseCase
import com.chiu.renovadoproyecto1.features.juegos.domain.usecase.GetJuegosUseCase
import com.chiu.renovadoproyecto1.features.juegos.domain.usecase.UpdateJuegoUseCase
import com.chiu.renovadoproyecto1.features.juegos.presentation.ViewModel.JuegosViewModelFactory
import com.chiu.renovadoproyecto1.features.login.di.AppModule

object JuegosModule {
    // Services
    private val juegosService: JuegosService by lazy {
        RetrofitHelper.getService(JuegosService::class.java)
    }

    // Repositories
    private val juegosRepository: JuegosRepository by lazy {
        JuegosRepositoryImpl(juegosService)
    }

    // Use Cases
    private val getJuegosUseCase: GetJuegosUseCase by lazy {
        GetJuegosUseCase(juegosRepository, AppModule.getTokenRepository)
    }

    private val createJuegoUseCase: CreateJuegoUseCase by lazy {
        CreateJuegoUseCase(juegosRepository, AppModule.getTokenRepository)
    }

    private val updateJuegoUseCase: UpdateJuegoUseCase by lazy {
        UpdateJuegoUseCase(juegosRepository, AppModule.getTokenRepository)
    }

    private val deleteJuegoUseCase: DeleteJuegoUseCase by lazy {
        DeleteJuegoUseCase(juegosRepository, AppModule.getTokenRepository)
    }

    // ViewModel Factory
    val juegosViewModelFactory: JuegosViewModelFactory by lazy {
        JuegosViewModelFactory(
            getJuegosUseCase,
            createJuegoUseCase,
            updateJuegoUseCase,
            deleteJuegoUseCase,
            AppModule.getTokenRepository
        )
    }
}