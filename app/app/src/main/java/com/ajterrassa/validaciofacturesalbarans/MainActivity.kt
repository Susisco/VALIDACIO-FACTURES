package com.ajterrassa.validaciofacturesalbarans

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.lifecycleScope
import com.ajterrassa.validaciofacturesalbarans.BuildConfig
import com.ajterrassa.validaciofacturesalbarans.data.network.ApiClient
import com.ajterrassa.validaciofacturesalbarans.data.repo.AlbaraPendingRepository
import com.ajterrassa.validaciofacturesalbarans.navigation.AppNavGraph
import com.ajterrassa.validaciofacturesalbarans.navigation.Rutes
import com.ajterrassa.validaciofacturesalbarans.ui.components.RootScaffold
import com.ajterrassa.validaciofacturesalbarans.ui.theme.ValidacioFacturesAlbaransTheme
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            try {
                val config = ApiClient.configService.getAppConfig()
                val minVersionCode = config.minSupportedVersion.filter { it.isDigit() }.toIntOrNull()
                if (minVersionCode != null && BuildConfig.VERSION_CODE < minVersionCode) {
                    val appUpdateManager = AppUpdateManagerFactory.create(this@MainActivity)
                    appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
                        if (info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                            info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                        ) {
                            appUpdateManager.startUpdateFlow(
                                info,
                                this@MainActivity,
                                AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                            )
                        }
                    }
                }
            } catch (_: Exception) {
                // Ignore errors
            }
        }

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
