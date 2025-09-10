// data/model/LoginResponse.kt
package com.ajterrassa.validaciofacturesalbarans.data.model

data class LoginResponse(
    val token: String,
    val nom: String, // pot ser "name" o "username" segons backend
    val id: Long, // pot ser "userId" o "id" segons backend
)
