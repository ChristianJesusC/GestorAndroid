package com.chiu.renovadoproyecto1.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "offline_juegos")
data class OfflineJuegoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String?,
    val compania: String?,
    val descripcion: String?,
    val cantidad: Int?,
    val logo: String?,
    val fechaCreacion: Long = System.currentTimeMillis()
)