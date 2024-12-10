package com.example.helloworld.ui

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.helloworld.logic.TicTacToeGame
import com.example.helloworld.navigation.Routes

@SuppressLint("ClickableViewAccessibility")
@Composable
fun TriquiScreen(navController: NavController) {
    val context = LocalContext.current
    val game = remember { TicTacToeGame() }
    val board = remember { mutableStateListOf(*game.getBoard().toTypedArray()) }
    var status by remember { mutableStateOf("Tu turno") }
    var gameOver by remember { mutableStateOf(false) }
    var showDifficultyDialog by remember { mutableStateOf(false) }

    // Actualiza el estado lógico del tablero en BoardView
    fun updateBoardView(boardView: BoardView) {
        boardView.setBoard(board.toCharArray())
    }

    // Verifica el ganador y actualiza el estado
    fun checkWinner() {
        when (game.checkForWinner()) {
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

    // Maneja los toques en las celdas del tablero
    fun onCellTouched(index: Int, boardView: BoardView) {
        if (!gameOver && board[index] == TicTacToeGame.OPEN_SPOT) {
            game.setMove(TicTacToeGame.HUMAN_PLAYER, index)
            board[index] = TicTacToeGame.HUMAN_PLAYER
            updateBoardView(boardView)
            checkWinner()
            if (!gameOver) {
                status = "Turno de la computadora"
                val computerMove = game.getComputerMove()
                board[computerMove] = TicTacToeGame.COMPUTER_PLAYER
                updateBoardView(boardView)
                checkWinner()
                if (!gameOver) status = "Tu turno"
            }
        }
    }

    // Reinicia el juego
    fun resetGame(boardView: BoardView) {
        game.clearBoard()
        for (i in board.indices) {
            board[i] = TicTacToeGame.OPEN_SPOT
        }
        status = "Tu turno"
        gameOver = false
        updateBoardView(boardView)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Dificultad - ${game.getDifficultyLevel()}",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )

        // Tablero usando BoardView
        AndroidView(
            factory = { context ->
                BoardView(context).apply {
                    setBoard(board.toCharArray())
                    setOnTouchListener { _, event ->
                        if (event.action == android.view.MotionEvent.ACTION_DOWN) {
                            val cellWidth = width / 3
                            val cellHeight = height / 3
                            val col = (event.x / cellWidth).toInt()
                            val row = (event.y / cellHeight).toInt()
                            val index = row * 3 + col
                            onCellTouched(index, this)
                            true
                        } else {
                            false
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        Text(
            text = status,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )

        // Botones de Reiniciar, Configuración y Volver
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { resetGame(BoardView(context)) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(text = "Reiniciar")
            }

            Button(
                onClick = { showDifficultyDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(text = "Dificultad")
            }

            Button(
                onClick = { navController.navigate(Routes.Main) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(text = "Volver")
            }
        }
    }

    // Dialogo para cambiar dificultad
    if (showDifficultyDialog) {
        AlertDialog(
            onDismissRequest = { showDifficultyDialog = false },
            title = { Text(text = "Selecciona la dificultad") },
            text = {
                Column {
                    TicTacToeGame.DifficultyLevel.entries.forEach { level ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RadioButton(
                                selected = game.getDifficultyLevel() == level,
                                onClick = {
                                    game.setDifficultyLevel(level)
                                    showDifficultyDialog = false
                                }
                            )
                            Text(text = level.name)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDifficultyDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}
