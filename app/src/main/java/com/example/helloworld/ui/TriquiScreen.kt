package com.example.helloworld.ui

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavController
import com.example.helloworld.logic.TicTacToeGame
import com.example.helloworld.navigation.Routes
import com.example.helloworld.R
import kotlinx.coroutines.flow.map
val Context.dataStore by preferencesDataStore(name = "game_preferences")

@SuppressLint("ClickableViewAccessibility")
@Composable
fun TriquiScreen(navController: NavController) {
    val context = LocalContext.current
    val game = remember { TicTacToeGame() }
    var board by rememberSaveable { mutableStateOf(game.getBoard().toList()) } // Tablero como List<Char>
    var status by rememberSaveable { mutableStateOf("Tu turno") }
    var gameOver by rememberSaveable { mutableStateOf(false) }
    var showDifficultyDialog by remember { mutableStateOf(false) }

    var humanWins by rememberSaveable { mutableStateOf(0) }
    var computerWins by rememberSaveable { mutableStateOf(0) }
    var ties by rememberSaveable { mutableStateOf(0) }

    val humanPlayerSound = MediaPlayer.create(context, R.raw.check)
    val computerPlayerSound = MediaPlayer.create(context, R.raw.mouse_click)

    // Claves para DataStore
    val boardKey = stringPreferencesKey("board_state")

    // Función para guardar el estado del tablero
    suspend fun saveBoardState() {
        val boardString = board.joinToString("") // Convertir List<Char> a String
        context.dataStore.edit { preferences ->
            preferences[boardKey] = boardString
        }
    }

    // Función para cargar el estado del tablero
    val boardFlow = context.dataStore.data.map { preferences ->
        preferences[boardKey]?.toList()?.map { it } ?: game.getBoard().toList()
    }
    val savedBoard by boardFlow.collectAsState(initial = game.getBoard().toList())

    // Actualizar el estado inicial del tablero desde DataStore
    LaunchedEffect(board) {
        val boardString = board.joinToString("") // Convertir List<Char> a String
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey("board_state")] = boardString
        }
    }


    fun checkWinner(): Int {
        return game.checkForWinner()
    }

    LaunchedEffect(gameOver) {
        if (gameOver) {
            when (checkWinner()) {
                1 -> ties++
                2 -> humanWins++
                3 -> computerWins++
            }
            context.dataStore.edit { preferences ->
                preferences[intPreferencesKey("humanWins")] = humanWins
                preferences[intPreferencesKey("computerWins")] = computerWins
                preferences[intPreferencesKey("ties")] = ties
            }
        }
    }

    fun onCellTouched(index: Int, boardView: BoardView) {
        if (!gameOver && index in board.indices && board[index] == TicTacToeGame.OPEN_SPOT) {
            game.setMove(TicTacToeGame.HUMAN_PLAYER, index)
            board = board.toMutableList().apply { this[index] = TicTacToeGame.HUMAN_PLAYER }
            humanPlayerSound.start()
            boardView.setBoard(board.toCharArray())
            val winner = checkWinner()
            if (winner != 0) {
                gameOver = true
                status = when (winner) {
                    1 -> "Empate"
                    2 -> "¡Ganaste!"
                    3 -> "La computadora ganó"
                    else -> "Error"
                }
            } else {
                status = "Turno de la computadora"
                Handler(Looper.getMainLooper()).postDelayed({
                    val computerMove = game.getComputerMove()
                    board = board.toMutableList().apply { this[computerMove] = TicTacToeGame.COMPUTER_PLAYER }
                    computerPlayerSound.start()
                    boardView.setBoard(board.toCharArray())
                    val compWinner = checkWinner()
                    if (compWinner != 0) {
                        gameOver = true
                        status = when (compWinner) {
                            1 -> "Empate"
                            2 -> "¡Ganaste!"
                            3 -> "La computadora ganó"
                            else -> "Error"
                        }
                    } else {
                        status = "Tu turno"
                    }
                }, 1000)
            }
        }
    }


    fun resetGame(boardView: BoardView) {
        game.clearBoard()
        board = game.getBoard().toList()
        status = "Tu turno"
        gameOver = false
        boardView.setBoard(board.toCharArray())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Human: $humanWins | Computer: $computerWins | Ties: $ties",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        // Tablero usando BoardView
        AndroidView(
            factory = { localContext ->
                BoardView(localContext).apply {
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
