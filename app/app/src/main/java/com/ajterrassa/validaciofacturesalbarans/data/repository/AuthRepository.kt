// data/repository/AuthRepository.kt
package com.ajterrassa.validaciofacturesalbarans.data.repository

import com.ajterrassa.validaciofacturesalbarans.data.model.LoginRequest
import com.ajterrassa.validaciofacturesalbarans.data.network.ApiClient

class AuthRepository {
    suspend fun login(username: String, password: String) =
        ApiClient.apiService.login(LoginRequest(username, password))
}
