package com.example.helloworld.logic

import kotlin.random.Random

class TicTacToeGame {
    companion object {
        const val HUMAN_PLAYER = 'X'
        const val COMPUTER_PLAYER = 'O'
        const val OPEN_SPOT = ' '
    }

    private val board = CharArray(9) { OPEN_SPOT }

    fun clearBoard() {
        for (i in board.indices) {
            board[i] = OPEN_SPOT
        }
    }

    fun setMove(player: Char, location: Int) {
        if (board[location] == OPEN_SPOT) {
            board[location] = player
        }
    }

    fun getComputerMove(): Int {
        // Lógica simple para el movimiento del computador, por ahora solo movimiento aleatorio
        var move: Int
        do {
            move = Random.nextInt(0, 9)
        } while (board[move] != OPEN_SPOT)
        setMove(COMPUTER_PLAYER, move)
        return move
    }

    fun checkForWinner(): Int {
        // Verificar si el humano o la computadora ha ganado o si es empate
        // Retorna: 0 - Sin ganador, 1 - Empate, 2 - Gana el humano, 3 - Gana la computadora
        // Filas
        for (i in 0..6 step 3) {
            if (board[i] == board[i + 1] && board[i + 1] == board[i + 2] && board[i] != OPEN_SPOT) {
                return if (board[i] == HUMAN_PLAYER) 2 else 3
            }
        }
        // Columnas
        for (i in 0 until 3) {
            if (board[i] == board[i + 3] && board[i + 3] == board[i + 6] && board[i] != OPEN_SPOT) {
                return if (board[i] == HUMAN_PLAYER) 2 else 3
            }
        }
        // Diagonales
        if ((board[0] == board[4] && board[4] == board[8] || board[2] == board[4] && board[4] == board[6]) && board[4] != OPEN_SPOT) {
            return if (board[4] == HUMAN_PLAYER) 2 else 3
        }
        // Empate
        return if (board.all { it != OPEN_SPOT }) 1 else 0
    }

    fun getBoard(): CharArray {
        return board
    }
}
