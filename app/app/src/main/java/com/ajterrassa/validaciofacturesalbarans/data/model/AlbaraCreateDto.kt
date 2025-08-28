package com.ajterrassa.validaciofacturesalbarans.data.model

data class AlbaraCreateDto(
    val tipus: String,
    val referenciaDocument: String,
    val data: String,
    val importTotal: Double,
    val estat: String,
    val creadorId: Long,
    val validatPerId: Long?,
    val proveidorId: Long,
    val edificiId: Long?,
    val otsId: Long?,
    val facturaId: Long?,
    val fitxerAdjunt: String? = null
)



