package com.example.helloworld.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.helloworld.data.EmpresaRepository

data class Empresa(val id: Int, val nombre: String, val clasificacion: String)

@Composable
fun EmpresasScreen(navController: NavController) {
    val context = LocalContext.current
    val repository = remember { EmpresaRepository(context) }
    val empresas = remember { mutableStateListOf<Empresa>() }
    var empresaToDelete by remember { mutableStateOf<Empresa?>(null) }

    LaunchedEffect(Unit) {
        empresas.addAll(repository.getAll()) // Cargar datos desde SQLite
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Lista de Empresas", style = MaterialTheme.typography.titleLarge)

        Button(
            onClick = { navController.navigate("empresa_form") },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Agregar Empresa")
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(empresas) { empresa ->
                EmpresaItem(
                    empresa = empresa,
                    onEdit = { navController.navigate("empresa_form?empresaId=${empresa.id}") },
                    onDelete = { empresaToDelete = empresa }
                )
            }
        }
    }

    if (empresaToDelete != null) {
        AlertDialog(
            onDismissRequest = { empresaToDelete = null },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Eliminar '${empresaToDelete?.nombre}'?") },
            confirmButton = {
                Button(onClick = {
                    repository.delete(empresaToDelete!!.id) // Eliminar de SQLite
                    empresas.remove(empresaToDelete)
                    empresaToDelete = null
                }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                Button(onClick = { empresaToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}


@Composable
fun EmpresaItem(empresa: Empresa, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = empresa.nombre, style = MaterialTheme.typography.bodyLarge)
                Text(text = empresa.clasificacion, style = MaterialTheme.typography.bodySmall)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onEdit) {
                    Text("Editar")
                }
                Button(onClick = onDelete) {
                    Text("Eliminar")
                }
            }
        }
    }
}
