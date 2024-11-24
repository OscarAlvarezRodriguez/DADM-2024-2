package com.example.helloworld.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.helloworld.ui.HelloScreen
import com.example.helloworld.ui.MainScreen
import com.example.helloworld.ui.TriquiScreen

object Routes {
    const val Main = "main_screen"
    const val Hello = "hello_screen"
    const val Triqui_off = "triqui_off_screen"
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.Main) {
        composable(Routes.Main) {
            MainScreen(
                navController = navController,
                title = "Inicio",
                content = {  Text("Contenido de Inicio")  }
            )
        }
        composable(Routes.Hello) {
            MainScreen(
                navController = navController,
                title = "Hello world",
                content = { HelloScreen(navController) }
            )
        }
        composable(Routes.Triqui_off) {
            MainScreen(
                navController = navController,
                title = "Hola",
                content = { TriquiScreen(navController) }
            )
        }
    }
}
