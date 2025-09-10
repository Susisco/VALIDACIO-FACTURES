package com.ajterrassa.validaciofacturesalbarans.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ajterrassa.validaciofacturesalbarans.data.repo.AlbaraPendingRepository

class UploadAlbaraWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val id = inputData.getLong("pending_id", -1L)
        val token = inputData.getString("token") ?: return Result.success() // evitem reintents

        if (id <= 0L) return Result.success()

        val repo = AlbaraPendingRepository(applicationContext)

        // Fase 1: crida sense scheduleOnFailure i sense reintents de WorkManager
        repo.tryUploadNow(id, token)

        // Tant si OK com si ERROR, deixem que la UI/refresc mostri l’estat actual.
        return Result.success()
    }
}
