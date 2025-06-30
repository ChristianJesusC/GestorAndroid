package com.chiu.renovadoproyecto1.core.di

import com.chiu.renovadoproyecto1.core.appcontext.AppContextHolder
import com.chiu.renovadoproyecto1.core.datastore.DataStoreManager

object DataStoreModule {
    val dataStoreManager: DataStoreManager by lazy {
        DataStoreManager(AppContextHolder.get())
    }
}