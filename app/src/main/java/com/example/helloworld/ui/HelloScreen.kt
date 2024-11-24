package com.example.helloworld.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.navigation.NavController
import com.example.helloworld.navigation.Routes

@Composable
fun HelloScreen(navController: NavController) {
    var currentColor by remember { mutableStateOf(rainbowColors.first()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentColor = rainbowColors.random()
            delay(200L)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Hello World",
            fontSize = 32.sp,
            color = currentColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )

        Button(
            onClick = { navController.navigate(Routes.Main) },
        ) {
            Text(text = "Volver")
        }
    }
}

val rainbowColors = listOf(
    Color.Red, Color(0xFFFFA500), Color.Yellow, Color.Green,
    Color.Blue, Color(0xFF4B0082), Color(0xFF8B00FF)
)
