package com.chiu.renovadoproyecto1.features.login.data.repository

import com.chiu.renovadoproyecto1.features.login.data.datasource.remote.LoginService
import com.chiu.renovadoproyecto1.features.login.data.model.LoginRequestDto
import com.chiu.renovadoproyecto1.features.login.domain.model.LoginResponse
import com.chiu.renovadoproyecto1.features.login.domain.model.User
import com.chiu.renovadoproyecto1.features.login.domain.repository.LoginRepository

class LoginRepositoryImpl(
    private val loginService: LoginService
) : LoginRepository {

    override suspend fun login(user: User): Result<LoginResponse> {
        return try {
            val loginRequest = LoginRequestDto(user.username, user.password)
            val response = loginService.login(loginRequest)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Error en login: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}