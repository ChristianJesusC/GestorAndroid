package com.chiu.renovadoproyecto1.core.http

import com.chiu.renovadoproyecto1.core.http.interceptor.AuthInterceptor
import com.chiu.renovadoproyecto1.core.http.interceptor.provideLoggingInterceptor
import com.chiu.renovadoproyecto1.core.datastore.DataStoreManager  // ← Cambio de import
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object RetrofitHelper {
    private const val BASE_URL = "http://34.231.32.231:3000/"
    private const val TIMEOUT = 30L

    private var retrofit: Retrofit? = null
    private var dataStoreManager: DataStoreManager? = null

    fun init(dataStore: DataStoreManager) {  // ← Cambio de parámetro
        dataStoreManager = dataStore
        if (retrofit == null) {
            synchronized(this) {
                if (retrofit == null) {
                    retrofit = buildRetrofit()
                }
            }
        }
    }

    fun <T> getService(serviceClass: Class<T>): T {
        requireNotNull(retrofit) { "RetrofitHelper no ha sido inicializado. Llama a init() primero." }
        return retrofit!!.create(serviceClass)
    }

    private fun buildRetrofit(): Retrofit {
        val client = buildHttpClient()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun buildHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(AuthInterceptor(requireNotNull(dataStoreManager)))  // ← Cambio aquí
            .addInterceptor(provideLoggingInterceptor())
            .build()
    }
}