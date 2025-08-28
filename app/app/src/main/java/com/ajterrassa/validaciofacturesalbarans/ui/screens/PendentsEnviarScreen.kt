package com.ajterrassa.validaciofacturesalbarans.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.ajterrassa.validaciofacturesalbarans.data.local.EstatPendent
import com.ajterrassa.validaciofacturesalbarans.data.local.PendingAlbaraEntity
import com.ajterrassa.validaciofacturesalbarans.data.repo.AlbaraPendingRepository
import com.ajterrassa.validaciofacturesalbarans.navigation.Rutes
import com.ajterrassa.validaciofacturesalbarans.ui.components.AppTopBar
import com.ajterrassa.validaciofacturesalbarans.ui.components.BottomNavBar
import kotlinx.coroutines.launch
import java.io.File

private val VermellAj = Color(0xFFD32F2F)
private val VermellAjClar = Color(0xFFFFF1F1)

@Composable
fun PendentsEnviarScreen(
    navController: NavController,
    repo: AlbaraPendingRepository
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    var pestanya by rememberSaveable { mutableStateOf(0) }
    val filtres = listOf("Tots", "Pendents", "Enviats", "Eliminats")

    // DAO i flux segons pestanya (recordat per evitar recomputacions innecessàries)
    val dao = remember { repo.db.pendingDao() }
    val llistaFlow = remember(pestanya) {
        when (pestanya) {
            1 -> dao.observeByStatus(EstatPendent.PENDENT)
            2 -> dao.observeByStatus(EstatPendent.ENVIAT)
            3 -> dao.observeByStatus(EstatPendent.ELIMINAT)
            else -> dao.observeAll()
        }
    }
    val llista by llistaFlow.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Històric local",
                navController = navController,
                showMenu = true
            )
        },
        bottomBar = {
            BottomNavBar(
                navController = navController,
                currentRoute = currentRoute,
                onLogoutConfirm = {
                    val prefs = ctx.getSharedPreferences("auth", Context.MODE_PRIVATE)
                    prefs.edit().clear().apply()
                    navController.navigate(Rutes.Login) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            ScrollableTabRow(selectedTabIndex = pestanya) {
                filtres.forEachIndexed { i, t ->
                    Tab(
                        selected = pestanya == i,
                        onClick = { pestanya = i },
                        text = { Text(t) }
                    )
                }
            }

            if (llista.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Sense registres")
                }
                return@Column
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(llista, key = { it.id }) { item ->
                    PendentCard(
                        item = item,
                        onOpen = { navController.navigate(Rutes.pendentPreview(item.id)) },
                        onEnviar = {
                            scope.launch {
                                val ok = repo.tryUploadNow(item.id, token(ctx))
                                Toast.makeText(
                                    ctx,
                                    if (ok) "Enviat" else "Error enviant",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        onEliminar = {
                            scope.launch {
                                repo.eliminarLocal(item.id)
                                Toast.makeText(ctx, "Eliminat", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PendentCard(
    item: PendingAlbaraEntity,
    onOpen: () -> Unit,
    onEnviar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() }
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = item.filePath?.let(::File),
                contentDescription = null,
                modifier = Modifier
                    .size(72.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(item.referencia, fontWeight = FontWeight.SemiBold)
                Text("Data: ${item.data}")
                Text("Import: ${"%.2f".format(item.importTotal)} €")
                EstatPill(item.status, item.lastError)
            }
            Spacer(Modifier.width(12.dp))

            when (item.status) {
                EstatPendent.ENVIANT -> {
                    CircularProgressIndicator(modifier = Modifier.size(28.dp))
                }
                EstatPendent.ENVIAT -> {
                    Text("Enviat", color = MaterialTheme.colorScheme.primary)
                }
                EstatPendent.ELIMINAT -> {
                    Text("Eliminat", color = MaterialTheme.colorScheme.error)
                }
                else -> {
                    Column(horizontalAlignment = Alignment.End) {
                        Button(
                            onClick = onEnviar,
                            colors = ButtonDefaults.buttonColors(containerColor = VermellAj)
                        ) { Text("Enviar ara", color = Color.White) }

                        Spacer(Modifier.height(6.dp))

                        if (item.status != EstatPendent.ENVIAT) {
                            OutlinedButton(onClick = onEliminar) { Text("Eliminar") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EstatPill(status: String, lastError: String?) {
    val (text, color) = when (status) {
        EstatPendent.PENDENT  -> "Pendent"  to MaterialTheme.colorScheme.secondary
        EstatPendent.ENVIANT  -> "Enviant"  to MaterialTheme.colorScheme.tertiary
        EstatPendent.ENVIAT   -> "Enviat"   to MaterialTheme.colorScheme.primary
        EstatPendent.ERROR    -> "Error"    to MaterialTheme.colorScheme.error
        EstatPendent.ELIMINAT -> "Eliminat" to MaterialTheme.colorScheme.outline
        else                  -> status     to MaterialTheme.colorScheme.outline
    }
    Row(
        modifier = Modifier
            .padding(top = 6.dp)
            .background(color.copy(alpha = 0.12f), shape = MaterialTheme.shapes.small)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text)
        if (status == EstatPendent.ERROR && !lastError.isNullOrBlank()) {
            Spacer(Modifier.width(6.dp))
            Text("· $lastError")
        }
    }
}

private fun token(ctx: Context): String {
    val prefs = ctx.getSharedPreferences("auth", Context.MODE_PRIVATE)
    return prefs.getString("token", "") ?: ""
}
