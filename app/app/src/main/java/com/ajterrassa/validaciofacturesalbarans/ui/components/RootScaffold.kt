package com.ajterrassa.validaciofacturesalbarans.ui.components

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ajterrassa.validaciofacturesalbarans.data.repo.AlbaraPendingRepository
import com.ajterrassa.validaciofacturesalbarans.navigation.AppNavGraph
import com.ajterrassa.validaciofacturesalbarans.navigation.Rutes

@Composable
fun RootScaffold(
    navController: NavHostController,
    repo: AlbaraPendingRepository
) {
    // Ruta actual
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBars = currentRoute != Rutes.Login

    Scaffold(
        topBar = {
            if (showBars) {
                AppTopBar(
                    title = when (currentRoute) {
                        Rutes.Pendents -> "Històric local"
                        Rutes.LlistaAlbaransEnviats -> "Albarans enviats"
                        Rutes.NouAlbara -> "Nou albarà"
                        else -> "Validació d'Albarans"
                    },
                    navController = navController,
                    showMenu = true
                )
            }
        },
        bottomBar = {
            if (showBars) {
                BottomNavBar(
                    // TIP: tipa BottomNavBar con NavController para evitar casts
                    navController = navController as NavController,
                    currentRoute = currentRoute,
                    onLogoutConfirm = {
                        val ctx: Context = navController.context
                        ctx.getSharedPreferences("auth", Context.MODE_PRIVATE)
                            .edit().clear().apply()
                        navController.navigate(Rutes.Login) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
        },
        containerColor = Color.White
    ) { innerPadding ->
        // 👇 Aquí sí va el grafo de navegación (una sola vez)
        Box(Modifier.padding(innerPadding)) {
            AppNavGraph(
                navController = navController,
                repo = repo,
                startDestination = Rutes.Pendents
            )
        }
    }
}
