package com.example.helloworld.data

import android.content.ContentValues
import android.content.Context
import com.example.helloworld.ui.Empresa

class EmpresaRepository(context: Context) {

    private val dbHelper = EmpresaDatabaseHelper(context)

    fun getAll(): List<Empresa> {
        val empresas = mutableListOf<Empresa>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            EmpresaDatabaseHelper.TABLE_EMPRESAS,
            null, null, null, null, null, null
        )
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(EmpresaDatabaseHelper.COLUMN_ID))
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow(EmpresaDatabaseHelper.COLUMN_NOMBRE))
            val clasificacion = cursor.getString(cursor.getColumnIndexOrThrow(EmpresaDatabaseHelper.COLUMN_CLASIFICACION))
            empresas.add(Empresa(id, nombre, clasificacion))
        }
        cursor.close()
        return empresas
    }

    fun insert(empresa: Empresa) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(EmpresaDatabaseHelper.COLUMN_NOMBRE, empresa.nombre)
            put(EmpresaDatabaseHelper.COLUMN_CLASIFICACION, empresa.clasificacion)
        }
        db.insert(EmpresaDatabaseHelper.TABLE_EMPRESAS, null, values)
    }

    fun update(empresa: Empresa) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(EmpresaDatabaseHelper.COLUMN_NOMBRE, empresa.nombre)
            put(EmpresaDatabaseHelper.COLUMN_CLASIFICACION, empresa.clasificacion)
        }
        db.update(
            EmpresaDatabaseHelper.TABLE_EMPRESAS,
            values,
            "${EmpresaDatabaseHelper.COLUMN_ID} = ?",
            arrayOf(empresa.id.toString())
        )
    }

    fun delete(id: Int) {
        val db = dbHelper.writableDatabase
        db.delete(
            EmpresaDatabaseHelper.TABLE_EMPRESAS,
            "${EmpresaDatabaseHelper.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
    }
}
