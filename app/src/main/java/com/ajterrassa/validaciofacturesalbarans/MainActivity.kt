package com.ajterrassa.validaciofacturesalbarans

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.launch
import com.ajterrassa.validaciofacturesalbarans.BuildConfig
// Ajusta estos 6 IMPORTS a tus paquetes reales:
import com.ajterrassa.validaciofacturesalbarans.data.network.ApiClient
import com.ajterrassa.validaciofacturesalbarans.navigation.Rutes
import com.ajterrassa.validaciofacturesalbarans.data.repo.AlbaraPendingRepository
import com.ajterrassa.validaciofacturesalbarans.ui.components.RootScaffold
import com.ajterrassa.validaciofacturesalbarans.ui.theme.ValidacioFacturesAlbaransTheme
import com.ajterrassa.validaciofacturesalbarans.navigation.AppNavGraph


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
                            info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
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

        val startDestination = Rutes.Splash

        setContent {
            val navController = rememberNavController()
            val repo = remember { AlbaraPendingRepository(applicationContext) }

            RootScaffold(navController = navController, repo = repo)

            ValidacioFacturesAlbaransTheme {
                val navController = rememberNavController()
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
