package com.ajterrassa.validaciofacturesalbarans.data.network

import android.content.Context
import android.util.Base64
import com.google.android.play.integrity.IntegrityManagerFactory
import com.google.android.play.integrity.IntegrityTokenRequest
import kotlinx.coroutines.tasks.await
import java.util.UUID

class IntegrityService(private val context: Context) {
    suspend fun requestToken(): String? {
        return try {
            val integrityManager = IntegrityManagerFactory.create(context)
            val nonce = Base64.encodeToString(UUID.randomUUID().toString().toByteArray(), Base64.NO_WRAP)
            val request = IntegrityTokenRequest.builder()
                .setNonce(nonce)
                .build()
            val response = integrityManager.requestIntegrityToken(request).await()
            response.token()
        } catch (e: Exception) {
            null
        }
    }
}

object IntegrityTokenProvider {
    @Volatile
    var token: String? = null
}
