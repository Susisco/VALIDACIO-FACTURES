package com.ajterrassa.validaciofacturesalbarans.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_albara")
data class PendingAlbaraEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val referencia: String,
    val data: String,
    val importTotal: Double,
    val proveidorId: Long,
    val filePath: String?,          // pot ser null si l’usuari elimina el fitxer
    val status: String,             // PENDENT | ENVIANT | ENVIAT | ERROR | ELIMINAT
    val retries: Int,
    val lastError: String?,
    val createdAt: Long
)

object EstatPendent {
    const val PENDENT = "PENDENT"
    const val ENVIANT = "ENVIANT"
    const val ENVIAT = "ENVIAT"
    const val ERROR = "ERROR"
    const val ELIMINAT = "ELIMINAT"
}
