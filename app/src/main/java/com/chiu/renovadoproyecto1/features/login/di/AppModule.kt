@file:Suppress("UNCHECKED_CAST")

package com.chiu.renovadoproyecto1.features.login.di

import com.chiu.renovadoproyecto1.core.http.RetrofitHelper
import com.chiu.renovadoproyecto1.features.login.data.datasource.remote.LoginService
import com.chiu.renovadoproyecto1.features.login.data.repository.LoginRepositoryImpl
import com.chiu.renovadoproyecto1.features.login.data.repository.TokenRepositoryImpl
import com.chiu.renovadoproyecto1.features.login.domain.repository.LoginRepository
import com.chiu.renovadoproyecto1.features.login.domain.repository.TokenRepository
import com.chiu.renovadoproyecto1.features.login.domain.usecase.LoginUseCase
import com.chiu.renovadoproyecto1.core.di.DataStoreModule

object AppModule {

    init {
        RetrofitHelper.init(DataStoreModule.dataStoreManager)
    }




    private val tokenRepository: TokenRepository by lazy {
        TokenRepositoryImpl(DataStoreModule.dataStoreManager)
    }

    private val loginRepository: LoginRepository by lazy {
        LoginRepositoryImpl(loginService)
    }

    private val loginService: LoginService by lazy {
        RetrofitHelper.getService(LoginService::class.java)
    }

    val loginUseCase: LoginUseCase by lazy {
        LoginUseCase(loginRepository, tokenRepository)
    }

    val getTokenRepository: TokenRepository by lazy {
        tokenRepository
    }
}