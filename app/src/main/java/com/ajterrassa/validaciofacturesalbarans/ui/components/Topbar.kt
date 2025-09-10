package com.ajterrassa.validaciofacturesalbarans.ui.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.ajterrassa.validaciofacturesalbarans.navigation.Rutes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import com.ajterrassa.validaciofacturesalbarans.R
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

private val VermellAj = Color(0xFFD32F2F)
private val VermellAjClar = Color(0xFFFFF1F1)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    navController: NavController,
    showMenu: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        title = {
            Box(modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.logo_ajuntament_terrassa),
                    contentDescription = "Logo",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(45.dp) // Adjust size as needed
                        .padding(top = 8.dp) // Add top padding
                        .align(Alignment.CenterStart)
                )

                Text(
                    text = title,
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White
                )
            }
        },
                actions = {
                    if (showMenu) {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menú")
                }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(VermellAjClar)
                        ) {
                    DropdownMenuItem(
                        text = { Text("Nou albarà") },
                        onClick = {
                            expanded = false
                            navController.navigate(Rutes.NouAlbara)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Històric local") },
                        onClick = {
                            expanded = false
                            navController.navigate(Rutes.Pendents)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Albarans enviats") },
                        onClick = {
                            expanded = false
                            navController.navigate(Rutes.LlistaAlbaransEnviats)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Tancar sessió") },
                        onClick = {
                            expanded = false
                            // Neteja token i torna a Login
                            val ctx = navController.context
                            ctx.getSharedPreferences("auth", Context.MODE_PRIVATE)
                                .edit().clear().apply()
                            navController.navigate(Rutes.Login) {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
           },
colors = TopAppBarDefaults.centerAlignedTopAppBarColors(

    containerColor = VermellAj,
    titleContentColor = Color.White
),
        modifier = Modifier.height(50.dp) // Set to desired thinness


    )
    }