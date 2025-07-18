package com.chiu.renovadoproyecto1.core.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.chiu.renovadoproyecto1.core.database.dao.OfflineJuegosDao
import com.chiu.renovadoproyecto1.core.database.entities.OfflineJuegoEntity

@Database(
    entities = [OfflineJuegoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun offlineJuegosDao(): OfflineJuegosDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gamestore_offline_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}