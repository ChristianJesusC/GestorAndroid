package com.chiu.renovadoproyecto1.core.notifications.domain.model

data class NotificationToken(
    val token: String,
    val platform: String = "android",
    val isActive: Boolean = true
)