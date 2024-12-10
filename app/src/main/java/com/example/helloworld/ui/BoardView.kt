package com.example.helloworld.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.example.helloworld.R

class BoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        const val GRID_WIDTH = 6 // Grosor de las líneas del tablero
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG) // Pintura para las líneas del tablero
    private var humanBitmap: Bitmap? = null // Imagen para el jugador humano (X)
    private var computerBitmap: Bitmap? = null // Imagen para la computadora (O)
    private var board: CharArray = CharArray(9) { ' ' } // Estado lógico del tablero

    init {
        // Cargar las imágenes para X y O
        humanBitmap = BitmapFactory.decodeResource(resources, R.drawable.o_image)
        computerBitmap = BitmapFactory.decodeResource(resources, R.drawable.x_image)

        // Configurar el pincel para las líneas del tablero
        paint.color = Color.LTGRAY
        paint.strokeWidth = GRID_WIDTH.toFloat()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        Log.d("BoardView", "Redibujando el tablero")
        super.onDraw(canvas)
        Log.d("BoardView", "Redibujando el tablero")
        val cellWidth = width / 3
        val cellHeight = height / 3

        // Dibujar líneas verticales
        for (i in 1..2) {
            canvas.drawLine(
                (i * cellWidth).toFloat(), 0f,
                (i * cellWidth).toFloat(), height.toFloat(),
                paint
            )
        }

        // Dibujar líneas horizontales
        for (i in 1..2) {
            canvas.drawLine(
                0f, (i * cellHeight).toFloat(),
                width.toFloat(), (i * cellHeight).toFloat(),
                paint
            )
        }

        // Dibujar las X y O en el tablero
        for (i in board.indices) {
            val col = i % 3
            val row = i / 3
            val left = col * cellWidth
            val top = row * cellHeight
            val right = left + cellWidth
            val bottom = top + cellHeight

            when (board[i]) {
                'X' -> canvas.drawBitmap(humanBitmap!!, null, Rect(left, top, right, bottom), null)
                'O' -> canvas.drawBitmap(computerBitmap!!, null, Rect(left, top, right, bottom), null)
            }
        }
    }

    fun setBoard(newBoard: CharArray) {
        board = newBoard
        invalidate() // Redibujar la vista
    }
}
