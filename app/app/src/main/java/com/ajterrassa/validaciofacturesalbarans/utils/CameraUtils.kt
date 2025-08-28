package com.ajterrassa.validaciofacturesalbarans.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

fun crearFitxerImatge(context: Context, fileName: String? = null): Pair<File, Uri> {
    val dir = File(context.filesDir, "fotos").apply { if (!exists()) mkdirs() }
    val name = fileName ?: "albara_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.jpg"
    val fitxer = File(dir, name)
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", fitxer)
    return fitxer to uri
}
