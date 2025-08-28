package com.ajterrassa.validaciofacturesalbarans.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.ajterrassa.validaciofacturesalbarans.data.repo.AlbaraPendingRepository
import com.ajterrassa.validaciofacturesalbarans.ui.screens.*

@Composable
fun AppNavGraph(
    navController: NavHostController,
    repo: AlbaraPendingRepository,
    startDestination: String
) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable(Rutes.Login) {
            LoginScreen(navController = navController)
        }

        composable(Rutes.Inici) {
            HomeScreen(navController = navController, repo = repo)
        }

        composable(Rutes.NouAlbara) {
            NouAlbaraScreen(navController = navController, repo = repo)
        }


        composable(Rutes.Pendents) { PendentsEnviarScreen(navController, repo) }

        composable(Rutes.LlistaAlbaransEnviats) {
            LlistaAlbaransScreen(navController = navController)
        }


        composable(
            route = Rutes.PendentPreview,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            PendentPreviewScreen(navController = navController, repo = repo, id = id)
        }




    }


}
