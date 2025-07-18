package com.chiu.renovadoproyecto1.core.database.dao

import androidx.room.*
import com.chiu.renovadoproyecto1.core.database.entities.OfflineJuegoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OfflineJuegosDao {

    @Query("SELECT * FROM offline_juegos ORDER BY fechaCreacion DESC")
    suspend fun getAllOfflineJuegos(): List<OfflineJuegoEntity>

    @Query("SELECT * FROM offline_juegos ORDER BY fechaCreacion DESC")
    fun getAllOfflineJuegosFlow(): Flow<List<OfflineJuegoEntity>>

    @Insert
    suspend fun insertOfflineJuego(juego: OfflineJuegoEntity): Long

    @Delete
    suspend fun deleteOfflineJuego(juego: OfflineJuegoEntity)

    @Query("DELETE FROM offline_juegos WHERE id = :id")
    suspend fun deleteOfflineJuegoById(id: Int)

    @Query("SELECT COUNT(*) FROM offline_juegos")
    suspend fun getOfflineJuegosCount(): Int

    @Query("SELECT COUNT(*) FROM offline_juegos")
    fun getOfflineJuegosCountFlow(): Flow<Int>
}