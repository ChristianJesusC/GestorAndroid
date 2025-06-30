package com.chiu.renovadoproyecto1.features.login.domain.repository

import com.chiu.renovadoproyecto1.features.login.domain.model.LoginResponse
import com.chiu.renovadoproyecto1.features.login.domain.model.User

interface LoginRepository {
    suspend fun login(user: User): Result<LoginResponse>
}