package com.example.helloworld.ui.triquiOnline

import android.annotation.SuppressLint
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.helloworld.logic.TicTacToeGame
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("HardwareIds")
@Composable
fun GameScreen(navController: NavController, gameId: String) {
    val database = FirebaseDatabase.getInstance().getReference("games/$gameId")
    val context = LocalContext.current
    val playerId = remember { Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) }

    val scope = rememberCoroutineScope()

    var board by remember { mutableStateOf(List(9) { " " }) }
    var turn by remember { mutableStateOf("player1") }
    var winner by remember { mutableStateOf<String?>(null) }
    var playerRole by remember { mutableStateOf<String?>(null) } // Puede ser "player1" o "player2"


    // Inicializar jugador y estado del juego
    LaunchedEffect(Unit) {
        database.get().addOnSuccessListener { snapshot ->
            val player1 = snapshot.child("player1").getValue(String::class.java)
            val player2 = snapshot.child("player2").getValue(String::class.java)
            board = snapshot.child("board").getValue<List<String>>()?.map { it } ?: List(9) { " " }
            turn = snapshot.child("turn").getValue(String::class.java) ?: "player1"
            winner = snapshot.child("winner").getValue(String::class.java)

            if (player1 == playerId) {
                playerRole = "player1"
            } else if (player2 == "waiting") {
                database.child("player2").setValue(playerId) // Convertirse en player2
                playerRole = "player2"
            } else if (player2 == playerId) {
                playerRole = "player2"
            } else {
                Log.e("GameScreen", "No puedes unirte a este juego")
                navController.popBackStack() // Regresar si no puede unirse
            }
        }
    }

    // Escuchar cambios en el juego en tiempo real
    LaunchedEffect(Unit) {
        database.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                board = snapshot.child("board").getValue<List<String>>()?.map { it } ?: List(9) { " " }
                turn = snapshot.child("turn").getValue(String::class.java) ?: "player1"
                winner = snapshot.child("winner").getValue(String::class.java)
            }


            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                Log.e("Firebase", "Error al escuchar cambios: ${error.message}")
            }
        })
    }

    // Función para realizar un movimiento
    fun makeMove(index: Int) {
        if (board[index] == " " && turn == playerRole && winner == "waiting") {
            val updatedBoard = board.toMutableList().apply {
                this[index] = if (playerRole == "player1") "X" else "O"
            }

            scope.launch(Dispatchers.IO) {
                database.child("board").setValue(updatedBoard)
                val nextTurn = if (turn == "player1") "player2" else "player1"
                database.child("turn").setValue(nextTurn)

                // Sincronizar TicTacToeGame con el tablero actualizado
                val ticTacToe = TicTacToeGame()
                updatedBoard.forEachIndexed { i, value ->
                    when (value) {
                        "X" -> ticTacToe.setMove(TicTacToeGame.HUMAN_PLAYER, i)
                        "O" -> ticTacToe.setMove(TicTacToeGame.COMPUTER_PLAYER, i)
                    }
                }

                // Verificar ganador después del movimiento
                when (ticTacToe.checkForWinner()) {
                    2 -> database.child("winner").setValue("player1") // Gana jugador 1
                    3 -> database.child("winner").setValue("player2") // Gana jugador 2
                    1 -> database.child("winner").setValue("tie") // Empate
                }
            }
        }
    }



    // UI del juego
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Turno de: $turn",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        // Mostrar el tablero
        Box(
            modifier = Modifier
                .size(300.dp)
                .background(Color.Gray, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                for (row in 0..2) {
                    Row(modifier = Modifier.weight(1f)) {
                        for (col in 0..2) {
                            val index = row * 3 + col
                            Button(
                                onClick = { makeMove(index) },
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                            ) {
                                Text(
                                    text = board[index],
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }

        // Mostrar el ganador
        if (winner != "waiting") {
            Text(
                text = when (winner) {
                    "player1" -> "¡Jugador 1 (X) gana!"
                    "player2" -> "¡Jugador 2 (O) gana!"
                    "tie" -> "¡Es un empate!"
                    else -> ""
                },
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
