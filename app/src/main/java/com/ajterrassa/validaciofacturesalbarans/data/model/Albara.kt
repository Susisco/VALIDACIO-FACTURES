package com.ajterrassa.validaciofacturesalbarans.data.model

data class Albara(
    val id: Long,
    val tipus: String,
    val referenciaDocument: String,
    val data: String,
    val importTotal: Double,
    val estat: String,
    val creadorId: Int,
    val validatPerId: Int?,
    val proveidorId: Long,
    val fitxerAdjunt: String?,
    val edificiId: Int?,
    val otsId: Int?,
    val facturaId: Int?,
    val dataCreacio: String
)
