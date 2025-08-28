package com.ajterrassa.validaciofacturesalbarans.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.ajterrassa.validaciofacturesalbarans.data.repo.AlbaraPendingRepository
import java.io.File

@Composable
fun PendentPreviewScreen(
    navController: NavController,
    repo: AlbaraPendingRepository,
    id: Long
){
    val item by repo.db.pendingDao().observeById(id).collectAsState(initial = null)

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable { navController.popBackStack() }
    ) {
        val path = item?.filePath
        if (!path.isNullOrBlank()) {
            val painter = rememberAsyncImagePainter(model = File(path))
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        } else {
            Text(
                text = if (item == null) "No trobat" else "Fitxer no disponible",
                color = MaterialTheme.colorScheme.inverseOnSurface,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
