package com.chiu.renovadoproyecto1.core.service.di

import android.content.Context
import com.chiu.renovadoproyecto1.core.di.OfflineModule
import com.chiu.renovadoproyecto1.core.service.data.repository.SyncRepositoryImpl
import com.chiu.renovadoproyecto1.core.service.domain.repository.SyncRepository
import com.chiu.renovadoproyecto1.core.service.domain.usecase.SyncOfflineDataUseCase
import com.chiu.renovadoproyecto1.core.service.domain.usecase.SyncOfflineDataUseCaseImpl
import com.chiu.renovadoproyecto1.features.juegos.di.JuegosModule

object ServiceModule {

    fun provideSyncRepository(context: Context): SyncRepository {
        val offlineRepository = OfflineModule.provideOfflineRepository(context)
        val createJuegoUseCase = JuegosModule.getCreateJuegoUseCase(context)

        return SyncRepositoryImpl(
            offlineRepository = offlineRepository,
            createJuegoUseCase = createJuegoUseCase
        )
    }

    fun provideSyncOfflineDataUseCase(context: Context): SyncOfflineDataUseCase {
        return SyncOfflineDataUseCaseImpl(
            syncRepository = provideSyncRepository(context)
        )
    }
}