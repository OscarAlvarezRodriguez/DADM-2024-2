package com.example.helloworld.ui.triquiOnline

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Composable
fun TriquiOnlineScreen(navController: NavController) {
    val database = FirebaseDatabase.getInstance().getReference("games") // Referencia a Firebase
    val context = LocalContext.current // Obtener el contexto correctamente dentro de la función @Composable
    val games = remember { mutableStateListOf<String>() } // Lista de juegos disponibles

    // Listener para actualizar la lista de juegos disponibles
    LaunchedEffect(Unit) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                games.clear() // Limpiamos la lista antes de actualizarla
                snapshot.children.forEach { gameSnapshot ->
                    val gameId = gameSnapshot.key
                    if (gameId != null) {
                        games.add(gameId)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error al cargar juegos: ${error.message}")
            }
        })
    }

    // Crear un nuevo juego
    @SuppressLint("HardwareIds")
    fun createGame(context: Context) {
        val playerId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        val gameId = database.push().key
        if (gameId != null) {
            database.child(gameId).setValue(
                mapOf(
                    "player1" to playerId,
                    "player2" to "waiting", // Marcador para esperar al jugador 2
                    "board" to List(9) { " " },
                    "turn" to "player1",
                    "winner" to "waiting"
                )
            ).addOnSuccessListener {
                Log.d("Firebase", "Juego creado con ID: $gameId")
                navController.navigate("game/$gameId")
            }.addOnFailureListener { error ->
                Log.e("Firebase", "Error al crear juego: ${error.message}")
            }
        }
    }

    // UI de la pantalla
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Botón para crear un nuevo juego
        Button(onClick = { createGame(context) }) {
            Text("Crear nuevo juego")
        }

        Text(text = "Lista de juegos disponibles", style = MaterialTheme.typography.titleLarge)

        // Mostrar cada juego como un botón
        games.forEach { gameId ->
            Button(
                onClick = { navController.navigate("game/$gameId") }, // Navegar al juego
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Unirse a juego: $gameId")
            }
        }

        if (games.isEmpty()) {
            Text(
                text = "No hay juegos disponibles. ¡Crea uno nuevo!",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
