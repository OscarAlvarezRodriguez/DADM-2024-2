package com.example.helloworld.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.helloworld.ui.HelloScreen
import com.example.helloworld.ui.MainScreen
import com.example.helloworld.ui.TriquiScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "main_screen") {
        composable("main_screen") { MainScreen(navController) }
        composable("hello_screen") { HelloScreen(navController) }
        composable("triqui_screen") { TriquiScreen(navController) }
    }
}
