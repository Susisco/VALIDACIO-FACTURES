package com.ajterrassa.validaciofacturesalbarans.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min

object ImatgeValidator {

    data class Result(val ok: Boolean, val message: String?)

    suspend fun validar(context: Context, uri: Uri): Result = withContext(Dispatchers.IO) {
        val bm = context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
            ?: return@withContext Result(false, "No puc obrir la imatge")

        // 1) Resolució mínima
        if (bm.width < 1200 || bm.height < 1600) return@withContext Result(false, "Resolució insuficient (mínim 1200×1600)")

        // 2) Aspect ratio ~ A4 vertical
        val ratio = bm.height.toFloat() / bm.width.toFloat()
        if (ratio < 1.3f || ratio > 1.6f) return@withContext Result(false, "Enquadra verticalment el document (A4)")

        // 3) Lluminositat
        val luma = lumaMitjana(bm)
        if (luma < 60 || luma > 230) return@withContext Result(false, "Il·luminació poc òptima. Evita reflexos.")

        // 4) Nitidesa (variància Laplacian)
        val varLap = laplacianVariance(bm)
        if (varLap < 60) return@withContext Result(false, "Foto borrosa. Mantén el mòbil estable i reintenta.")

        // 5) Presència de text
        val image = InputImage.fromBitmap(bm, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val res = Tasks.await(recognizer.process(image))
        val chars = res.text?.length ?: 0
        if (chars < 10) return@withContext Result(false, "No detecto text suficient. Apropa’t i reencuadra.")

        Result(true, null)
    }

    private fun lumaMitjana(bm: Bitmap): Int {
        var s = 0L
        val step = max(1, min(bm.width, bm.height) / 800)
        var count = 0
        for (y in 0 until bm.height step step) {
            for (x in 0 until bm.width step step) {
                val c = bm.getPixel(x, y)
                val r = (c shr 16) and 0xFF
                val g = (c shr 8) and 0xFF
                val b = c and 0xFF
                val yLuma = (0.2126 * r + 0.7152 * g + 0.0722 * b).toInt()
                s += yLuma; count++
            }
        }
        return (s / max(1, count)).toInt()
    }

    private fun laplacianVariance(bm: Bitmap): Double {
        val step = max(1, min(bm.width, bm.height) / 600)
        val w = bm.width; val h = bm.height
        val values = ArrayList<Int>()
        fun gray(x: Int, y: Int): Int {
            val c = bm.getPixel(x, y)
            val r = (c shr 16) and 0xFF
            val g = (c shr 8) and 0xFF
            val b = c and 0xFF
            return ((r + g + b) / 3)
        }
        for (y in step until h - step step step) {
            for (x in step until w - step step step) {
                val c = 4*gray(x,y) - gray(x-step,y) - gray(x+step,y) - gray(x,y-step) - gray(x,y+step)
                values.add(c)
            }
        }
        val mean = values.average()
        return values.fold(0.0) { acc, v -> acc + (v - mean)*(v - mean) } / values.size.coerceAtLeast(1)
    }
}
