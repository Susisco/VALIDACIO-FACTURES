package com.ajterrassa.validaciofacturesalbarans.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.ajterrassa.validaciofacturesalbarans.data.network.IntegrityService
import com.ajterrassa.validaciofacturesalbarans.data.network.IntegrityTokenProvider
import com.ajterrassa.validaciofacturesalbarans.navigation.Rutes

@Composable
fun SplashScreen(navController: NavController) {
    val context = navController.context
    LaunchedEffect(Unit) {
        val service = IntegrityService(context)
        val token = service.requestToken()
        IntegrityTokenProvider.token = token
        val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
        val hasToken = !prefs.getString("token", null).isNullOrEmpty()
        val destination = if (hasToken) Rutes.Inici else Rutes.Login
        navController.navigate(destination) {
            popUpTo(0) { inclusive = true }
        }
    }
    Box(modifier = Modifier.fillMaxSize())
}
