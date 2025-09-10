package com.ajterrassa.validaciofacturesalbarans.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ajterrassa.validaciofacturesalbarans.data.repo.AlbaraPendingRepository
import com.ajterrassa.validaciofacturesalbarans.navigation.Rutes
import com.ajterrassa.validaciofacturesalbarans.ui.components.AppTopBar
import com.ajterrassa.validaciofacturesalbarans.ui.components.BottomNavBar

private val VermellAj = Color(0xFFD32F2F)
private val VermellAjClar = Color(0xFFFFF1F1)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController, repo: AlbaraPendingRepository
) {

    val context = LocalContext.current
    val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    val nom = prefs.getString("nom", "Usuari") ?: "Usuari"
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Menú principal", navController = navController
            )
        },
                bottomBar = {
            BottomNavBar(
                navController = navController,
                currentRoute = currentRoute,
                onLogoutConfirm = { /* your logout code */ }
                // isLoginScreen is false by default, so you can omit it
            )
        }

    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Benvingut!\n$nom!",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
                modifier = Modifier.align(Alignment.CenterHorizontally)

            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = { navController.navigate(Rutes.NouAlbara) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(containerColor = VermellAj)
            )
            {
                Text(text = "🆕 Nou Albarà", color = Color.White, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))


            Button(
                onClick = { navController.navigate(Rutes.Pendents) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(containerColor = VermellAj)
            ) { Text("📂 Històric en local", color = Color.White) }

            Button(
                onClick = { navController.navigate(Rutes.LlistaAlbaransEnviats) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(containerColor = VermellAj)

            ) {
                Text("\uD83D\uDCE4 Els meus albarans enviats (backend)")
            }

        }
    }
}
