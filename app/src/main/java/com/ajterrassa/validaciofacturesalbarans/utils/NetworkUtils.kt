package com.ajterrassa.validaciofacturesalbarans.utils

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.*

/** Convierte un Uri a File temporal */
@Throws(IOException::class)
fun uriToFile(context: Context, uri: Uri): File {
    val input = context.contentResolver.openInputStream(uri) ?: throw IOException("Cannot open URI")
    val tempFile = File(context.cacheDir, "upload_temp")
    FileOutputStream(tempFile).use { output ->
        input.copyTo(output)
    }
    return tempFile
}

/** Crea el MultipartBody.Part para el campo “file” */
fun createMultipartFilePart(fieldName: String, file: File): MultipartBody.Part {
    val mediaType = when (file.extension.lowercase()) {
        "jpg","jpeg" -> "image/jpeg"
        "png"        -> "image/png"
        else         -> "application/octet-stream"
    }.toMediaTypeOrNull()

    val body: RequestBody = file.readBytes().toRequestBody(mediaType)
    return MultipartBody.Part.createFormData(fieldName, file.name, body)
}
