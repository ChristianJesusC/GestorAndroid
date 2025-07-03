package com.chiu.renovadoproyecto1.features.juegos.data.model

data class ApiResponse<T>(
    val success: Boolean,
    val data: T,
    val message: String? = null
)