package com.example.helloworld.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.helloworld.ui.HelloScreen
import com.example.helloworld.ui.MainScreen
import com.example.helloworld.ui.TriquiScreen
import com.example.helloworld.ui.ContainerMainScreen
import com.example.helloworld.ui.triquiOnline.GameScreen
import com.example.helloworld.ui.triquiOnline.TriquiOnlineScreen

object Routes {
    const val Main = "main_screen"
    const val Hello = "hello_screen"
    const val Triqui_off = "triqui_off_screen"
    const val Triqui_on = "triqui_on_screen"
    const val Game = "game/{gameId}" // Ruta dinámica
}


@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.Main) {
        composable(Routes.Main) {
            ContainerMainScreen(
                navController = navController,
                title = "Inicio",
                content = { MainScreen(navController) }
            )
        }
        composable(Routes.Hello) {
            ContainerMainScreen(
                navController = navController,
                title = "Hello world",
                content = { HelloScreen(navController) }
            )
        }
        composable(Routes.Triqui_off) {
            ContainerMainScreen(
                navController = navController,
                title = "Triqui - Tic Tac Toe - Offline",
                content = { TriquiScreen(navController) }
            )
        }
        composable(Routes.Triqui_on) {
            ContainerMainScreen(
                navController = navController,
                title = "Triqui - Tic Tac Toe - Online",
                content = { TriquiOnlineScreen(navController) }
            )
        }
        composable(
            route = Routes.Game,
            arguments = listOf(navArgument("gameId") { nullable = false })
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getString("gameId") ?: ""
            ContainerMainScreen(
                navController = navController,
                title = "Juego: $gameId",
                content = { GameScreen(navController, gameId) }
            )
        }
    }
}
