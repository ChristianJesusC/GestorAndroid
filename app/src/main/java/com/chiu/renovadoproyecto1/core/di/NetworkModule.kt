package com.chiu.renovadoproyecto1.core.di

import android.content.Context
import com.chiu.renovadoproyecto1.core.network.data.repository.NetworkRepositoryImpl
import com.chiu.renovadoproyecto1.core.network.data.usecase.CheckNetworkUseCaseImpl
import com.chiu.renovadoproyecto1.core.network.domain.repository.NetworkRepository
import com.chiu.renovadoproyecto1.core.network.domain.usecase.CheckNetworkUseCase

object NetworkModule {

    fun provideNetworkRepository(context: Context): NetworkRepository {
        return NetworkRepositoryImpl(context)
    }

    fun provideCheckNetworkUseCase(context: Context): CheckNetworkUseCase {
        val repository = provideNetworkRepository(context)
        return CheckNetworkUseCaseImpl(repository)
    }
}