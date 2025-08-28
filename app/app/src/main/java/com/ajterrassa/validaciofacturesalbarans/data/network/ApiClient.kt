package com.ajterrassa.validaciofacturesalbarans.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    //private const val BASE_URL = "http://10.0.2.2:8080"
    private const val BASE_URL = "https://validacio-backend.fly.dev"

    //private const val BASE_URL_PROD = "https://validaciofactures.ajterrassa.cat"
//Para usar la IP de tu móvil en vez de 10.0.2.2 (emulador), debes poner la IP local de tu PC
// (a la que tu móvil pueda acceder en la misma red WiFi). Por ejemplo, si tu PC tiene la IP 192.168.1.100,
// cambia la constante así:
  //private const val BASE_URL = "http://192.168.1.133:8080"
//  Asegúrate de que tu móvil y tu PC estén en la misma red y que el firewall permita conexiones al puerto 8080.


    // Logging para debug
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY

        // Per desenvolupar, millor BASIC o HEADERS (NO BODY amb binaris grossos)
        level = HttpLoggingInterceptor.Level.BASIC
    }

    // Cliente OkHttp SIN añadir aquí el header Authorization
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS) // pujada d’imatge
        .retryOnConnectionFailure(true)
        // Si sospites d'HTTP/2 amb el backend/proxy, força HTTP/1.1:
        //.protocols(listOf(Protocol.HTTP_1_1))
        .build()

    // Retrofit + ApiService
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiService::class.java)
    }
}
