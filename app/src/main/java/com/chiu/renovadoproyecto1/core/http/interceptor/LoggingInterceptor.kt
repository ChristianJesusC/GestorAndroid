package com.chiu.renovadoproyecto1.core.http.interceptor

import okhttp3.logging.HttpLoggingInterceptor

fun provideLoggingInterceptor(): HttpLoggingInterceptor {
    return HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
}