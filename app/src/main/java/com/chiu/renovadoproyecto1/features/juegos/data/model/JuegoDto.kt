package com.chiu.renovadoproyecto1.features.juegos.data.model

import com.chiu.renovadoproyecto1.features.juegos.domain.model.Juego

data class JuegoDto(
    val id: Int? = null,
    val nombre: String? = null,
    val compania: String? = null,
    val descripcion: String? = null,
    val cantidad: Int? = null
) {
    fun toDomain() = Juego(
        id = this.id,
        nombre = this.nombre,
        compania = this.compania,
        descripcion = this.descripcion,
        cantidad = this.cantidad
    )
}