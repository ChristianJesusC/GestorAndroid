package com.chiu.renovadoproyecto1.core.di

import android.content.Context
import com.chiu.renovadoproyecto1.core.offline.data.repository.OfflineRepositoryImpl
import com.chiu.renovadoproyecto1.core.offline.data.usecase.SaveOfflineJuegoUseCaseImpl
import com.chiu.renovadoproyecto1.core.offline.domain.repository.OfflineRepository
import com.chiu.renovadoproyecto1.core.offline.domain.usecase.SaveOfflineJuegoUseCase

object OfflineModule {

    fun provideOfflineRepository(context: Context): OfflineRepository {
        val offlineJuegosDao = DatabaseModule.provideOfflineJuegosDao(context)
        return OfflineRepositoryImpl(offlineJuegosDao)
    }

    fun provideSaveOfflineJuegoUseCase(context: Context): SaveOfflineJuegoUseCase {
        return SaveOfflineJuegoUseCaseImpl(provideOfflineRepository(context))
    }
}