package com.ajterrassa.validaciofacturesalbarans.data.network

import com.ajterrassa.validaciofacturesalbarans.data.model.Albara
import com.ajterrassa.validaciofacturesalbarans.data.model.LoginRequest
import com.ajterrassa.validaciofacturesalbarans.data.model.LoginResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("/api/auth/login") // ✅ correcte
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("/api/albarans/me/mobile")
    suspend fun getAlbarans(@Header("Authorization") token: String): List<Albara>



    @Multipart
    @POST("api/albarans/app/save-with-file")
    fun uploadAlbara(
        @Header("Authorization") auth: String,
        @Part("data") jsonData: RequestBody,
        @Part filePart: MultipartBody.Part
    ): retrofit2.Call<Any>

}
