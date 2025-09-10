package com.ajterrassa.validaciofacturesalbarans.data.network

import android.content.Context
import android.util.Base64
import kotlinx.coroutines.tasks.await
import java.util.UUID
import com.google.android.play.integrity.IntegrityManagerFactory
import com.google.android.play.integrity.IntegrityTokenRequest

class IntegrityService(private val context: Context) {
    suspend fun requestToken(): String? = runCatching {
        val manager = com.google.android.play.integrity.IntegrityManagerFactory.create(context)

        val nonce = Base64.encodeToString(
            UUID.randomUUID().toString().toByteArray(),
            Base64.NO_WRAP
        )

        val request = com.google.android.play.integrity.IntegrityTokenRequest.builder()
            .setNonce(nonce)
            .build()

        val response = manager.requestIntegrityToken(request).await()
        response.token()
    }.getOrNull()
}

object IntegrityTokenProvider {
    @Volatile var token: String? = null
}
