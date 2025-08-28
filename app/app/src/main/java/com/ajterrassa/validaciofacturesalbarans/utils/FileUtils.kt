package com.ajterrassa.validaciofacturesalbarans.utils

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

fun crearMultipartFitxer(file: File?): MultipartBody.Part {
    val requestFile = file?.let {
        RequestBody.create("image/*".toMediaTypeOrNull(), it)
    }
    return MultipartBody.Part.createFormData("file", file?.name, requestFile!!)
}
