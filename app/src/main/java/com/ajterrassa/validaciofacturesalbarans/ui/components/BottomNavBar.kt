package com.ajterrassa.validaciofacturesalbarans.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ajterrassa.validaciofacturesalbarans.navigation.Rutes
import androidx.compose.material.icons.filled.ArrowBack

private val VermellAj = Color(0xFFD32F2F)
private val VermellAjClar = Color(0xFFFFF1F1)

@Composable
fun BottomNavBar(
    navController: NavController,
    currentRoute: String?,
    onLogoutConfirm: () -> Unit,
    isLoginScreen: Boolean = false,
    onCloseApp: (() -> Unit)? = null // Only for login
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    NavigationBar(modifier = Modifier.height(50.dp), containerColor = VermellAj, contentColor = Color.White) {
        if (isLoginScreen) {
                NavigationBarItem(
                selected = false,
                onClick = { onCloseApp?.invoke() },
                icon = { Icon(Icons.Outlined.ExitToApp, contentDescription = "Cerrar aplicación") },
                label = { Text("Cerrar") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    indicatorColor = VermellAj,
                    unselectedIconColor = Color.White.copy(alpha = 0.8f),
                    unselectedTextColor = Color.White.copy(alpha = 0.8f)
                )
            )
        } else {

            // Back button (left)
            NavigationBarItem(
                selected = false,
                onClick = { navController.popBackStack() },
                icon = { Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    indicatorColor = VermellAj,
                    unselectedIconColor = Color.White.copy(alpha = 0.8f),
                    unselectedTextColor = Color.White.copy(alpha = 0.8f)
                )
            )
            //home button (middle)
        NavigationBarItem(
            selected = currentRoute == Rutes.Pendents,
            onClick = { if (currentRoute != Rutes.Pendents) navController.navigate(Rutes.Inici) },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Inici") },
            //label = { Text("Inici") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color.White,
                indicatorColor = VermellAj,
                unselectedIconColor = Color.White.copy(alpha = 0.8f),
                unselectedTextColor = Color.White.copy(alpha = 0.8f)
            )
        )
            // Send button (right)
        NavigationBarItem(
            selected = false,
            onClick = { showLogoutDialog = true },
            icon = { Icon(Icons.Outlined.ExitToApp, contentDescription = "Tancar sessió") },
            //label = { Text("Sortir") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color.White,
                indicatorColor = VermellAj,
                unselectedIconColor = Color.White.copy(alpha = 0.8f),
                unselectedTextColor = Color.White.copy(alpha = 0.8f)
        )
        )

    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Tancar sessió") },
            text = { Text("Segur que vols tancar la sessió?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogoutConfirm()
                        navController.navigate(Rutes.Login) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }                    }
                ) { Text("Sí, sortir") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel·lar")
                }
            },
            containerColor = VermellAjClar
        )
    }
}
}
