package com.ajterrassa.validaciofacturesalbarans.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ajterrassa.validaciofacturesalbarans.data.model.Albara
import com.ajterrassa.validaciofacturesalbarans.data.network.ApiClient
import com.ajterrassa.validaciofacturesalbarans.navigation.Rutes
import com.ajterrassa.validaciofacturesalbarans.ui.components.AppTopBar
import com.ajterrassa.validaciofacturesalbarans.ui.components.BottomNavBar
import kotlinx.coroutines.launch
import retrofit2.HttpException
import androidx.compose.ui.Alignment
import java.text.SimpleDateFormat
import java.util.Locale

private val VermellAj = Color(0xFFD32F2F)
private val VermellAjClar = Color(0xFFFFF1F1)

@Composable
fun LlistaAlbaransScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    var albarans by remember { mutableStateOf<List<Albara>>(emptyList()) }
    var expandedId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) {
        val token = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
            .getString("token", null)

        if (token == null) {
            Toast.makeText(context, "Sessió caducada", Toast.LENGTH_LONG).show()
            navController.navigate(Rutes.Login) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
            return@LaunchedEffect
        }

        scope.launch {
            try {
                albarans = ApiClient.apiService.getAlbarans("Bearer $token")
                    .sortedByDescending { it.dataCreacio }
            } catch (e: HttpException) {
                Toast.makeText(context, "Error ${e.code()}", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Albarans enviats",
                navController = navController,
                showMenu = true
            )
        },
        bottomBar = {
            BottomNavBar(
                navController = navController,
                currentRoute = currentRoute,
                onLogoutConfirm = {
                    val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
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
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            if (albarans.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No hi ha albarans enviats.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = VermellAj
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(albarans) { albara ->
                        val isExpanded = expandedId == albara.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expandedId = if (isExpanded) null else albara.id
                                },
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Data creació: ${formatDataCreacio(albara.dataCreacio)}",
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "Referència: ${albara.referenciaDocument}",
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (isExpanded) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("ID: ${albara.id}")
                                    Text("Data: ${albara.data}")
                                    Text("Estat: ${albara.estat}")
                                    Text("Import total: ${albara.importTotal}")
                                    Text("Data creació: ${albara.dataCreacio}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun formatDataCreacio(isoString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
        val date = inputFormat.parse(isoString)
        val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        if (date != null) outputFormat.format(date) else ""
    } catch (e: Exception) {
        ""
    }
}
