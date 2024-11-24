package com.example.helloworld.logic

import kotlin.random.Random

class TicTacToeGame {

    enum class DifficultyLevel { Easy, Harder, Expert }

    private var difficultyLevel: DifficultyLevel = DifficultyLevel.Expert

    fun getDifficultyLevel(): DifficultyLevel {
        return difficultyLevel
    }

    fun setDifficultyLevel(level: DifficultyLevel) {
        difficultyLevel = level
    }

    companion object {
        const val HUMAN_PLAYER = 'X'
        const val COMPUTER_PLAYER = 'G'
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

    private fun getRandomMove(): Int {
        var move: Int
        do {
            move = Random.nextInt(0, 9)
        } while (board[move] != OPEN_SPOT)
        setMove(COMPUTER_PLAYER, move)
        return move
    }

    private fun getWinningMove(): Int {
        for (i in board.indices) {
            if (board[i] == OPEN_SPOT) {
                // Simula hacer un movimiento
                board[i] = COMPUTER_PLAYER
                // Verifica si gana con este movimiento
                if (checkForWinner() == 3) { // indica que la computadora gana
                    board[i] = OPEN_SPOT // Restablece el tablero
                    setMove(COMPUTER_PLAYER, i)
                    return i
                }
                board[i] = OPEN_SPOT // Restablece el tablero
            }
        }
        return -1 // No hay movimiento ganador
    }


    private fun getBlockingMove(): Int {
        for (i in board.indices) {
            if (board[i] == OPEN_SPOT) {
                // Simula hacer un movimiento del humano
                board[i] = HUMAN_PLAYER
                // Verifica si el humano ganaría con este movimiento
                if (checkForWinner() == 2) { // indica que el humano gana
                    board[i] = OPEN_SPOT // Restablece el tablero
                    setMove(COMPUTER_PLAYER, i)
                    return i
                }
                board[i] = OPEN_SPOT // Restablece el tablero
            }
        }
        return -1 // No hay necesidad de bloquear
    }


    fun getComputerMove(): Int {
        return when (difficultyLevel) {
            DifficultyLevel.Easy -> getRandomMove()

            DifficultyLevel.Harder -> {
                getBlockingMove().takeIf { it != -1 }
                    ?: getRandomMove()
            }

            DifficultyLevel.Expert -> {
                getWinningMove().takeIf { it != -1 }
                    ?: getBlockingMove().takeIf { it != -1 }
                    ?: getRandomMove()
            }
        }
    }


    fun checkForWinner(): Int {
        // 0 sin ganador
        // 1 Empate
        // 2 Humano
        // 3 Computadora
        // Verificar filas
        for (i in 0..6 step 3) {
            if (board[i] == board[i + 1] && board[i + 1] == board[i + 2] && board[i] != OPEN_SPOT) {
                return if (board[i] == HUMAN_PLAYER) 2 else 3
            }
        }

        // Verificar columnas
        for (i in 0 until 3) {
            if (board[i] == board[i + 3] && board[i + 3] == board[i + 6] && board[i] != OPEN_SPOT) {
                return if (board[i] == HUMAN_PLAYER) 2 else 3
            }
        }

        // Verificar diagonales
        // Diagonal 0 -> 4 -> 8
        if (board[0] == board[4] && board[4] == board[8] && board[0] != OPEN_SPOT) {
            return if (board[0] == HUMAN_PLAYER) 2 else 3
        }
        // Diagonal 2 -> 4 -> 6
        if (board[2] == board[4] && board[4] == board[6] && board[2] != OPEN_SPOT) {
            return if (board[2] == HUMAN_PLAYER) 2 else 3
        }

        // Verificar empate
        if (board.all { it != OPEN_SPOT }) {
            return 1 // Empate
        }

        // Sin ganador
        return 0
    }


    fun getBoard(): CharArray {
        return board
    }
}
