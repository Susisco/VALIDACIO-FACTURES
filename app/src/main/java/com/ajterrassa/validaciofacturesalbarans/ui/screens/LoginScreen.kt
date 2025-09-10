package com.ajterrassa.validaciofacturesalbarans.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.ajterrassa.validaciofacturesalbarans.data.network.ApiClient
import com.ajterrassa.validaciofacturesalbarans.ui.components.AppTopBar
//data class LoginRequest(val email: String, val contrasenya: String)
//data class LoginResponse(val token: String, val usuariId: Long)
import retrofit2.HttpException
import java.io.IOException
import com.ajterrassa.validaciofacturesalbarans.data.model.LoginRequest
import com.ajterrassa.validaciofacturesalbarans.data.model.LoginResponse
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.Alignment
import androidx.compose.material3.TextFieldDefaults
import com.ajterrassa.validaciofacturesalbarans.ui.components.BottomNavBar
import com.ajterrassa.validaciofacturesalbarans.data.model.DeviceRegistrationRequest
import com.ajterrassa.validaciofacturesalbarans.data.model.DeviceRegistrationStatus
import com.ajterrassa.validaciofacturesalbarans.data.network.FidProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.installations.FirebaseInstallations
import kotlinx.coroutines.tasks.await


private val DarkBlue = Color(0xFF1565C0)
private val LightBlue = Color(0xFFE3F2FD)
private val VermellAj = Color(0xFFD32F2F)
private val VermellAjClar = Color(0xFFFFCDD2)



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    val context = navController.context

    LaunchedEffect(Unit) {
        try {
            FirebaseApp.initializeApp(context)
            val fid = FirebaseInstallations.getInstance().id.await()
            FidProvider.fid = fid
            val status = withContext(Dispatchers.IO) {
                ApiClient.apiService.registerDevice(DeviceRegistrationRequest(fid))
            }
            if (status == DeviceRegistrationStatus.PENDING || status == DeviceRegistrationStatus.REVOKED) {
                val msg = if (status == DeviceRegistrationStatus.PENDING) {
                    "Dispositiu pendent d'aprovació"
                } else {
                    "Accés revocat"
                }
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            }
        } catch (_: Exception) {
        }
    }

    Scaffold(
        topBar = { AppTopBar(title = "Enviar albarà", navController = navController, showMenu = false) },
        bottomBar = {
            BottomNavBar(
                navController = navController,
                currentRoute = null,
                onLogoutConfirm = {},
                isLoginScreen = true,
                onCloseApp = { (navController.context as? Activity)?.finishAffinity() }
            )
        }
    )  { padding ->
    // Contingut principal
        Column(
                         modifier = Modifier
                    .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(100.dp))

Text(
    "Inicia sessió",
    style = MaterialTheme.typography.headlineMedium,
    color = Color.Black,
    modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally))

            Spacer(modifier = Modifier.height(24.dp))
            // Camps de login
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedIndicatorColor = Color.Black,
                    unfocusedIndicatorColor = Color(0xFF333333),
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color(0xFF333333)
                )
            )

            var showPassword by remember { mutableStateOf(false) }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contrasenya") },
                singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {

                    val image = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (showPassword) "Oculta contrasenya" else "Mostra contrasenya"
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(imageVector = image, contentDescription = description)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
 colors = TextFieldDefaults.colors(
     focusedTextColor = Color.Black,
     cursorColor = Color.Black,
     focusedIndicatorColor = Color.Black,
     unfocusedIndicatorColor = Color(0xFF333333),
     focusedLabelColor = Color.Black,
     unfocusedLabelColor = Color(0xFF333333)
 )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    loading = true
                },
                enabled = !loading && email.isNotBlank() && password.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VermellAj)
            ) { Text(if (loading) "Entrant..." else "Inicia sessió") }
        }
    }

    // Acció de login (fora de la jerarquia per no recompondre)
    if (loading) {
        LaunchedEffect(email, password) {
            val ok = doLoginAndStore(navController, email.trim(), password)
            loading = false
            if (!ok) {
                Toast.makeText(
                    navController.context,
                    "Credencials incorrectes o error de xarxa",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

private suspend fun doLoginAndStore(
    navController: NavController,
    email: String,
    password: String
): Boolean = withContext(Dispatchers.IO) {
    try {
        val api = ApiClient.apiService

        // ApiService.login és suspend i retorna directament LoginResponse
        val body: LoginResponse = api.login(LoginRequest(email = email, contrasenya = password))

        // Desa token i usuariId
        val prefs = navController.context.getSharedPreferences("auth", android.content.Context.MODE_PRIVATE)
        prefs.edit()
            .putString("token", body.token)
            .putInt("usuariId", body.id.toInt())
            .apply()

        // Navega a Home
        withContext(Dispatchers.Main) {
            navController.navigate(com.ajterrassa.validaciofacturesalbarans.navigation.Rutes.Inici) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
        true
    } catch (e: HttpException) {
        // 4xx/5xx
        false
    } catch (e: IOException) {
        // problemes de xarxa
        false
    } catch (e: Exception) {
        false
    }
}
