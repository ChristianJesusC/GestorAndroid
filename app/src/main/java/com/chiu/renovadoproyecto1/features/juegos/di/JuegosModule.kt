@file:Suppress("UNCHECKED_CAST")

package com.chiu.renovadoproyecto1.features.juegos.di

import android.content.Context
import com.chiu.renovadoproyecto1.core.di.NetworkModule  // ✅ Importar NetworkModule
import com.chiu.renovadoproyecto1.core.hardware.di.HardwareModule
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

    private val juegosService: JuegosService by lazy {
        RetrofitHelper.getService(JuegosService::class.java)
    }

    private fun getJuegosRepository(context: Context): JuegosRepository {
        return JuegosRepositoryImpl(juegosService, context)
    }

    fun getGetJuegosUseCase(context: Context): GetJuegosUseCase {
        return GetJuegosUseCase(getJuegosRepository(context), AppModule.getTokenRepository)
    }

    fun getCreateJuegoUseCase(context: Context): CreateJuegoUseCase {
        return CreateJuegoUseCase(getJuegosRepository(context), AppModule.getTokenRepository)
    }

    fun getUpdateJuegoUseCase(context: Context): UpdateJuegoUseCase {
        return UpdateJuegoUseCase(getJuegosRepository(context), AppModule.getTokenRepository)
    }

    fun getDeleteJuegoUseCase(context: Context): DeleteJuegoUseCase {
        return DeleteJuegoUseCase(getJuegosRepository(context), AppModule.getTokenRepository)
    }

    fun getJuegosViewModelFactory(context: Context): JuegosViewModelFactory {
        val capturePhotoUseCase = HardwareModule.getCapturePhotoUseCase(
            context,
            context as androidx.fragment.app.FragmentActivity
        )

        val checkNetworkUseCase = NetworkModule.provideCheckNetworkUseCase(context)

        return JuegosViewModelFactory(
            getJuegosUseCase = getGetJuegosUseCase(context),
            createJuegoUseCase = getCreateJuegoUseCase(context),
            updateJuegoUseCase = getUpdateJuegoUseCase(context),
            deleteJuegoUseCase = getDeleteJuegoUseCase(context),
            tokenRepository = AppModule.getTokenRepository,
            capturePhotoUseCase = capturePhotoUseCase,
            checkNetworkUseCase = checkNetworkUseCase  // ✅ Inyectar network use case
        )
    }
}