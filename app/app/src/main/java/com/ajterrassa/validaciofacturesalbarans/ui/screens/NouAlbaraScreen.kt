package com.ajterrassa.validaciofacturesalbarans.ui.screens

import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.core.content.ContextCompat
import com.ajterrassa.validaciofacturesalbarans.data.repo.AlbaraPendingRepository
import com.ajterrassa.validaciofacturesalbarans.navigation.Rutes
import com.ajterrassa.validaciofacturesalbarans.ui.components.AppTopBar
import com.ajterrassa.validaciofacturesalbarans.ui.components.BottomNavBar
import com.ajterrassa.validaciofacturesalbarans.utils.ImatgeValidator
import com.ajterrassa.validaciofacturesalbarans.utils.crearFitxerImatge
import com.ajterrassa.validaciofacturesalbarans.utils.uriToFile
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private val VermellAj = Color(0xFFD32F2F)
private val VermellAjClar = Color(0xFFFFF1F1)
private val FieldBackground = Color.White

@Composable
fun NouAlbaraScreen(
    navController: NavController,
    repo: AlbaraPendingRepository
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Formats per defecte
    val now = remember { Date() }
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val refFormat = remember { SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()) }

    // Valors per defecte
    val defaultReferencia = remember { "PENDENT-${refFormat.format(now)}" }
    val defaultData = remember { dateFormat.format(now) }
    val defaultProveidor = "1"
    val defaultImportTotal = "0.00"

    // Estat del formulari
    var referencia by remember { mutableStateOf(defaultReferencia) }
    var data by remember { mutableStateOf(defaultData) }
    var proveidor by remember { mutableStateOf(defaultProveidor) }
    var importTotal by remember { mutableStateOf(defaultImportTotal) }

    // Estat de foto i procés
    var fotoUri by remember { mutableStateOf<Uri?>(null) }
    var fotoFile by remember { mutableStateOf<File?>(null) }
    var isBusy by remember { mutableStateOf(false) }
    var pendingStartCamera by remember { mutableStateOf(false) }

    // Línia de navegació actual per a la BottomNavBar
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    // Launcher per fer foto
    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && fotoUri != null) {
            scope.launch {
                isBusy = true
                try {
                    // Validació bàsica
                    val r = ImatgeValidator.validar(context, fotoUri!!)
                    if (!r.ok) {
                        Toast.makeText(context, r.message ?: "Foto no vàlida", Toast.LENGTH_LONG).show()
                        return@launch
                    }

                    val file = try {
                        uriToFile(context, fotoUri!!)
                    } catch (e: IOException) {
                        Toast.makeText(context, "Error obrint imatge", Toast.LENGTH_LONG).show()
                        return@launch
                    }

                    val ref = referencia.trim()
                    val dataStr = data.trim()
                    val imp = importTotal.toDoubleOrNull() ?: 0.0
                    val provId = (proveidor.toLongOrNull() ?: 0L)

                    // Desa com a pendent (offline/cola)
                    repo.enqueuePending(file, ref, dataStr, imp, provId)

                    Toast.makeText(context, "Afegit a Pendents", Toast.LENGTH_SHORT).show()
                    // Opcional: anar a pendents
                    // navController.navigate(Rutes.Pendents)
                } finally {
                    isBusy = false
                }
            }
        } else {
            Toast.makeText(context, "Foto cancel·lada", Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher per permisos de càmera
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted && pendingStartCamera) {
            pendingStartCamera = false
            val nom = "albara_${defaultReferencia}_${refFormat.format(Date())}.jpg"
            val (file, uri) = crearFitxerImatge(context, nom)
            fotoFile = file
            fotoUri = uri
            takePictureLauncher.launch(fotoUri)
        } else if (!granted) {
            Toast.makeText(context, "Cal autoritzar l'ús de la càmera", Toast.LENGTH_LONG).show()
        }
    }

    // ---------- UI amb Scaffold: TopBar + BottomNav ----------
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Nou albarà",
                navController = navController,
                showMenu = true
            )
        },
        bottomBar = {
            BottomNavBar(
                navController = navController,
                currentRoute = currentRoute,
                onLogoutConfirm = {
                    // Aquí pots implementar el logout real si vols (SharedPreferences/Datastore)
                    // i la navegació a login (si no ho tens ja centralitzat)
                }
            )
        },
        containerColor = Color.White
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Títol contextual dins el cos si vols remarcar
                Text(
                    "Dades per defecte (no cal editar ara)",
                    color = VermellAj,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(12.dp))

                // Referència
                OutlinedTextField(
                    value = referencia,
                    onValueChange = { referencia = it },
                    label = { Text("Referència") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(FieldBackground, shape = MaterialTheme.shapes.medium),
                    singleLine = true
                )

                Spacer(Modifier.height(8.dp))

                // Data
                OutlinedTextField(
                    value = data,
                    onValueChange = {
                        if (it.matches(Regex("""\d{0,4}-?\d{0,2}-?\d{0,2}"""))) data = it
                    },
                    label = { Text("Data (YYYY-MM-DD)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(FieldBackground, shape = MaterialTheme.shapes.medium),
                    singleLine = true
                )

                Spacer(Modifier.height(8.dp))

                // Proveïdor (deshabilitat de moment)
                OutlinedTextField(
                    value = proveidor,
                    onValueChange = { /* deshabilitat ara per ara */ },
                    label = { Text("Proveïdor ID") },
                    enabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(FieldBackground, shape = MaterialTheme.shapes.medium),
                    singleLine = true
                )

                Spacer(Modifier.height(8.dp))

                // Import Total (2 decimals)
                OutlinedTextField(
                    value = importTotal,
                    onValueChange = { txt ->
                        if (txt.matches(Regex("""\d{0,}(\.\d{0,2})?"""))) {
                            importTotal = when {
                                txt.startsWith(".") -> "0.${txt.removePrefix(".").take(2)}"
                                txt.contains(".") -> {
                                    val p = txt.split(".")
                                    val e = p[0].ifEmpty { "0" }
                                    val d = p.getOrNull(1) ?: ""
                                    if (d.length == 1) "$e.$d" else "$e.${d.take(2)}"
                                }
                                txt.isEmpty() -> "0.00"
                                else -> "$txt.00"
                            }
                        }
                    },
                    label = { Text("Import Total (€)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(FieldBackground, shape = MaterialTheme.shapes.medium),
                    singleLine = true
                )

                Spacer(Modifier.height(20.dp))

                Text(
                    "Foto seleccionada: ${fotoUri?.lastPathSegment.orEmpty().ifEmpty { "Cap" }}",
                    color = Color.DarkGray
                )

                Spacer(Modifier.height(12.dp))

                // Botó: Fer foto
                Button(
                    onClick = {
                        val hasCamera = ContextCompat.checkSelfPermission(
                            context, android.Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED

                        if (hasCamera) {
                            val nom = "albara_${defaultReferencia}_${refFormat.format(Date())}.jpg"
                            val (file, uri) = crearFitxerImatge(context, nom)
                            fotoFile = file
                            fotoUri = uri
                            takePictureLauncher.launch(fotoUri)
                        } else {
                            pendingStartCamera = true
                            cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VermellAj)
                ) { Text("📷 Fer foto", color = Color.White) }

                Spacer(Modifier.height(16.dp))

                // Botó: Veure pendents
                OutlinedButton(
                    onClick = { navController.navigate(Rutes.Pendents) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) { Text("📂 Veure pendents d’enviar") }
            }

            // Overlay de càrrega
            if (isBusy) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color(0x60000000))
                ) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
            }
        }
    }
}
