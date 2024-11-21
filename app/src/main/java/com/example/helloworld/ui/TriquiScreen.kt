package com.example.helloworld.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.helloworld.logic.TicTacToeGame

@Composable
fun TriquiScreen(navController: NavController) {
    val game = remember { TicTacToeGame() }
    val board = remember { mutableStateListOf(*game.getBoard().toTypedArray()) }
    var status by remember { mutableStateOf("Tu turno") }
    var gameOver by remember { mutableStateOf(false) }

    fun checkWinner() {
        when (val winner = game.checkForWinner()) {
            1 -> {
                status = "Empate"
                gameOver = true
            }
            2 -> {
                status = "¡Ganaste!"
                gameOver = true
            }
            3 -> {
                status = "La computadora ganó"
                gameOver = true
            }
        }
    }

    fun onButtonClick(index: Int) {
        if (!gameOver && board[index] == TicTacToeGame.OPEN_SPOT) {
            game.setMove(TicTacToeGame.HUMAN_PLAYER, index)
            board[index] = TicTacToeGame.HUMAN_PLAYER
            checkWinner()
            if (!gameOver) {
                status = "Turno de la computadora"
                val computerMove = game.getComputerMove()
                board[computerMove] = TicTacToeGame.COMPUTER_PLAYER
                checkWinner()
                if (!gameOver) status = "Tu turno"
            }
        }
    }

    fun resetGame() {
        game.clearBoard()
        for (i in board.indices) {
            board[i] = TicTacToeGame.OPEN_SPOT
        }
        status = "Tu turno"
        gameOver = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Triqui - Tic Tac Toe",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )

        // Tablero 3x3
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (row in 0..2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (col in 0..2) {
                        val index = row * 3 + col
                        Button(
                            onClick = { onButtonClick(index) },
                            enabled = board[index] == TicTacToeGame.OPEN_SPOT && !gameOver,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = when (board[index]) {
                                    TicTacToeGame.HUMAN_PLAYER -> MaterialTheme.colorScheme.primary
                                    TicTacToeGame.COMPUTER_PLAYER -> MaterialTheme.colorScheme.secondary
                                    else -> MaterialTheme.colorScheme.secondary
                                },
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.size(100.dp)
                        ) {
                            Text(
                                text = board[index].toString(),
                                fontSize = 24.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        Text(
            text = status,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )

        // Botones de Reiniciar y Regresar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { resetGame() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(text = "Reiniciar")
            }

            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(text = "Volver")
            }
        }
    }
}
