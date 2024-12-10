package com.example.helloworld.ui

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.database.getValue

data class Message(val text: String = "Holi :V")

@Composable
fun TriquiOnlineScreen(navController: NavController) {
    val messageState = remember { mutableStateOf("Cargando mensaje...") }

    // Referencia a la base de datos de Firebase
    val database = FirebaseDatabase.getInstance().getReference("message")

    // Usamos LaunchedEffect para cargar los datos al iniciar la pantalla
    LaunchedEffect(Unit) {
        database.get().addOnSuccessListener { snapshot ->
            val message = snapshot.getValue(String::class.java)
            messageState.value = message ?: "No se pudo obtener el mensaje"
        }.addOnFailureListener { exception ->
            Log.e("FirebaseError", "Error al cargar el mensaje", exception)
            messageState.value = "Error al cargar el mensaje"
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Triqui online", modifier = Modifier.padding(16.dp))
        Text(text = messageState.value, modifier = Modifier.padding(16.dp))
    }
}
