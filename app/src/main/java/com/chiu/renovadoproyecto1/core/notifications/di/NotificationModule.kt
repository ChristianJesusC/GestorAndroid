package com.chiu.renovadoproyecto1.core.notifications.di

import android.content.Context
import com.chiu.renovadoproyecto1.core.di.DataStoreModule
import com.chiu.renovadoproyecto1.core.http.RetrofitHelper
import com.chiu.renovadoproyecto1.core.notifications.data.datasource.remote.NotificationService
import com.chiu.renovadoproyecto1.core.notifications.data.repository.NotificationRepositoryImpl
import com.chiu.renovadoproyecto1.core.notifications.domain.repository.NotificationRepository
import com.chiu.renovadoproyecto1.core.notifications.domain.usecase.GetFCMTokenUseCase
import com.chiu.renovadoproyecto1.core.notifications.domain.usecase.RegisterTokenUseCase
import com.chiu.renovadoproyecto1.features.login.di.AppModule

object NotificationModule {

    private val notificationService: NotificationService by lazy {
        RetrofitHelper.getService(NotificationService::class.java)
    }

    fun provideNotificationRepository(context: Context): NotificationRepository {
        return NotificationRepositoryImpl(
            notificationService = notificationService,
            dataStoreManager = DataStoreModule.dataStoreManager
        )
    }

    fun provideGetFCMTokenUseCase(): GetFCMTokenUseCase {
        return GetFCMTokenUseCase()
    }

    fun provideRegisterTokenUseCase(context: Context): RegisterTokenUseCase {
        return RegisterTokenUseCase(
            notificationRepository = provideNotificationRepository(context),
            tokenRepository = AppModule.getTokenRepository
        )
    }
}