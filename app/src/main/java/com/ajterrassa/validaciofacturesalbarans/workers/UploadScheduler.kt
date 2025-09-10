package com.ajterrassa.validaciofacturesalbarans.workers

import android.content.Context
import androidx.work.*

import java.util.concurrent.TimeUnit

object UploadScheduler {
    fun scheduleRetry(ctx: Context, pendingId: Long) {
        val req = OneTimeWorkRequestBuilder<UploadAlbaraWorker>()
            .setInputData(workDataOf("pendingId" to pendingId))
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 15, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(ctx).enqueueUniqueWork("retry-$pendingId", ExistingWorkPolicy.REPLACE, req)
    }
}
