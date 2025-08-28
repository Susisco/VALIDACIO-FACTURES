package com.ajterrassa.validaciofacturesalbarans.data.network

import com.ajterrassa.validaciofacturesalbarans.data.model.AppConfig
import retrofit2.http.GET

interface ConfigService {
    @GET("/config/app")
    suspend fun getAppConfig(): AppConfig
}
