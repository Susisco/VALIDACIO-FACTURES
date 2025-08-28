package com.ajterrassa.validaciofacturesalbarans

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.ajterrassa.validaciofacturesalbarans.data.repo.AlbaraPendingRepository
import com.ajterrassa.validaciofacturesalbarans.navigation.AppNavGraph
import com.ajterrassa.validaciofacturesalbarans.navigation.Rutes
import com.ajterrassa.validaciofacturesalbarans.ui.components.RootScaffold
import com.ajterrassa.validaciofacturesalbarans.ui.theme.ValidacioFacturesAlbaransTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Decideix la pantalla d'inici segons token
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        val hasToken = !prefs.getString("token", null).isNullOrEmpty()
        val startDestination = if (hasToken) Rutes.Inici else Rutes.Login

        setContent {
            val navController = rememberNavController() // <-- es NavHostController
            val repo = remember { AlbaraPendingRepository(applicationContext) }

            RootScaffold(navController=navController,repo=repo )                 // lo pasamos como NavController a screens


            ValidacioFacturesAlbaransTheme {
                val navController = rememberNavController()
                // Un sol repo per tota l’app
                val repo = remember { AlbaraPendingRepository(applicationContext) }

                AppNavGraph(
                    navController = navController,
                    repo = repo,
                    startDestination = startDestination
                )
            }
        }
    }
}
