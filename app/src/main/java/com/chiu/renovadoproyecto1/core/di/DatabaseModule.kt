package com.chiu.renovadoproyecto1.core.di

import android.content.Context
import com.chiu.renovadoproyecto1.core.database.AppDatabase
import com.chiu.renovadoproyecto1.core.database.dao.OfflineJuegosDao

object DatabaseModule {

    fun provideAppDatabase(context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    fun provideOfflineJuegosDao(context: Context): OfflineJuegosDao {
        return provideAppDatabase(context).offlineJuegosDao()
    }
}