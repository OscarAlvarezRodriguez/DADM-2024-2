package com.example.helloworld.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MainScreen(navController: NavController) {
    // Matriz de nombres de botones definir aqui y en los if
    val buttonNames = listOf(
        listOf("Hello world", "Triqui"),
        /*listOf("Configuración", "Amigos"),
        listOf("Mensajes", "Notificaciones"),
        listOf("Tareas", "Calendario"),
        listOf("Estadísticas", "Ayuda")*/
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Desarrollo de Aplicaciones para Dispositivos Móviles",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(5) { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    repeat(2) { column ->
                        val buttonName = if (row < buttonNames.size && column < buttonNames[row].size) {
                            buttonNames[row][column]
                        } else {
                            "Aún no está..."
                        }

                        Button(
                            onClick = {
                                if (buttonName == "Hello world") {
                                    navController.navigate("hello_screen")
                                }
                                if (buttonName == "Triqui") {
                                    navController.navigate("triqui_screen")
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(text = buttonName)
                        }
                    }
                }
            }
        }

        Text(
            text = "Oscar Eduardo Alvarez Rodriguez",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
    }
}
