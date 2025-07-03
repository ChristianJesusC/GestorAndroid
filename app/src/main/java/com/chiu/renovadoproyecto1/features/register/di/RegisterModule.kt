@file:Suppress("UNCHECKED_CAST")

package com.chiu.renovadoproyecto1.features.register.di

import com.chiu.renovadoproyecto1.core.http.RetrofitHelper
import com.chiu.renovadoproyecto1.features.register.data.datasource.remote.RegisterService
import com.chiu.renovadoproyecto1.features.register.data.repository.RegisterRepositoryImpl
import com.chiu.renovadoproyecto1.features.register.domain.repository.RegisterRepository
import com.chiu.renovadoproyecto1.features.register.domain.usecase.RegisterUseCase
import com.chiu.renovadoproyecto1.core.di.DataStoreModule

object RegisterModule {

    init {
        RetrofitHelper.init(DataStoreModule.dataStoreManager)
    }

    private val registerRepository: RegisterRepository by lazy {
        RegisterRepositoryImpl(registerService)
    }

    private val registerService: RegisterService by lazy {
        RetrofitHelper.getService(RegisterService::class.java)
    }

    val registerUseCase: RegisterUseCase by lazy {
        RegisterUseCase(registerRepository)
    }
}