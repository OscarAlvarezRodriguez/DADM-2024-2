package com.example.helloworld.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.helloworld.data.EmpresaRepository

@Composable
fun EmpresaFormScreen(navController: NavController, empresaId: Int? = null) {
    val context = LocalContext.current
    val repository = remember { EmpresaRepository(context) }
    var nombre by remember { mutableStateOf("") }
    var clasificacion by remember { mutableStateOf("") }

    LaunchedEffect(empresaId) {
        if (empresaId != null) {
            val empresa = repository.getAll().find { it.id == empresaId }
            if (empresa != null) {
                nombre = empresa.nombre
                clasificacion = empresa.clasificacion
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = clasificacion,
            onValueChange = { clasificacion = it },
            label = { Text("Clasificación") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                if (empresaId == null) {
                    repository.insert(Empresa(0, nombre, clasificacion)) // Insertar nueva empresa
                } else {
                    repository.update(Empresa(empresaId, nombre, clasificacion)) // Actualizar empresa
                }
                navController.popBackStack()
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Guardar")
        }
    }
}
