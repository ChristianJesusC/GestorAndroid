package com.chiu.renovadoproyecto1.features.juegos.data.model

import com.chiu.renovadoproyecto1.features.juegos.domain.model.Juego

data class JuegoDto(
    val id: Int? = null,
    val nombre: String? = null,
    val compania: String? = null,
    val descripcion: String? = null,
    val cantidad: Int? = null,
    val logo: String? = null
) {
    fun toDomain() = Juego(
        id = this.id,
        nombre = this.nombre,
        compania = this.compania,
        descripcion = this.descripcion,
        cantidad = this.cantidad,
        logo = this.logo?.let { logoPath ->
            if (logoPath.startsWith("/uploads/")) {
                "http://98.86.41.104:3000$logoPath"
            } else {
                logoPath
            }
        }
    )
}