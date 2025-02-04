package com.example.helloworld.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.*
import org.json.JSONArray
import java.net.URL

data class ReporteCovid(
    val fechaReporte: String,
    val departamento: String,
    val ciudad: String,
    val recuperado: String,
    val tipoContagio: String,
    val sexo: String,
    val edad: Int
)

@Composable
fun WebServiceScreen(navController: NavHostController) {
    val context = LocalContext.current
    var reportes by remember { mutableStateOf(emptyList<ReporteCovid>()) }
    var filteredReportes by remember { mutableStateOf(emptyList<ReporteCovid>()) }
    var isLoading by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    // Variables para filtros
    var selectedSexo by remember { mutableStateOf("Todos") }
    var edadMin by remember { mutableStateOf(0) }
    var edadMax by remember { mutableStateOf(100) }
    var selectedCiudad by remember { mutableStateOf("Todas") }
    val ciudadesDisponibles = remember { mutableStateListOf("Todas") }
    var showFilters by remember { mutableStateOf(false) }

    // Aplicar los filtros seleccionados
    fun applyFilters() {
        filteredReportes = reportes.filter { reporte ->
            (selectedSexo == "Todos" || reporte.sexo == selectedSexo) &&
                    (reporte.edad in edadMin..edadMax) &&
                    (selectedCiudad == "Todas" || reporte.ciudad == selectedCiudad)
        }
    }

    // Función para obtener los datos del servicio web
    fun fetchData() {
        isLoading = true
        showError = false
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = "https://www.datos.gov.co/resource/gt2j-8ykr.json"
                val response = URL(url).readText()
                val jsonArray = JSONArray(response)
                val listaReportes = mutableListOf<ReporteCovid>()
                val ciudadesSet = mutableSetOf<String>()

                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(i)
                    val edad = item.optString("edad", "0").toIntOrNull() ?: 0

                    val reporte = ReporteCovid(
                        fechaReporte = item.optString("fecha_reporte_web", "Desconocido"),
                        departamento = item.optString("departamento_nom", "Desconocido"),
                        ciudad = item.optString("ciudad_municipio_nom", "Desconocido"),
                        recuperado = item.optString("recuperado", "No especificado"),
                        tipoContagio = item.optString("fuente_tipo_contagio", "No especificado"),
                        sexo = item.optString("sexo", "No especificado"),
                        edad = edad
                    )

                    listaReportes.add(reporte)
                    ciudadesSet.add(reporte.ciudad)
                }

                withContext(Dispatchers.Main) {
                    reportes = listaReportes
                    ciudadesDisponibles.clear()
                    ciudadesDisponibles.add("Todas")
                    ciudadesDisponibles.addAll(ciudadesSet)
                    isLoading = false
                    applyFilters() // Aplicar filtros con los datos obtenidos
                }
            } catch (e: Exception) {
                Log.e("WebService", "Error obteniendo datos: ${e.message}")
                withContext(Dispatchers.Main) {
                    showError = true
                    isLoading = false
                    Toast.makeText(context, "Error al obtener los datos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Cargar los datos al iniciar la pantalla
    LaunchedEffect(Unit) {
        fetchData()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showFilters = true }) {
                Icon(imageVector = Icons.Default.FilterList, contentDescription = "Filtros")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(text = "Reportes COVID-19", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (showError) {
                Text(
                    text = "Error al obtener los datos. Verifica tu conexión.",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredReportes) { reporte ->
                        ReporteItem(reporte)
                    }
                }
            }
        }
    }

    if (showFilters) {
        FilterSheet(
            selectedSexo, { selectedSexo = it },
            edadMin, { edadMin = it },
            edadMax, { edadMax = it },
            ciudadesDisponibles, selectedCiudad, { selectedCiudad = it },
            onClose = {
                showFilters = false
                applyFilters()
            }
        )
    }
}

@Composable
fun ReporteItem(reporte: ReporteCovid) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "📅 ${reporte.fechaReporte}", fontWeight = FontWeight.Bold)
            Text(text = "📍 ${reporte.ciudad}, ${reporte.departamento}", fontSize = 14.sp)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "🧑 ${reporte.sexo} | Edad: ${reporte.edad}", fontSize = 14.sp)
                Text(
                    text = if (reporte.recuperado == "Recuperado") "✅ Recuperado" else "⚠️ No Recuperado",
                    fontWeight = FontWeight.Bold,
                    color = if (reporte.recuperado == "Recuperado") Color.Green else Color.Red
                )
            }

            Text(
                text = "🔗 Tipo de Contagio: ${reporte.tipoContagio}",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSheet(
    selectedSexo: String, onSexoSelected: (String) -> Unit,
    edadMin: Int, onEdadMinChanged: (Int) -> Unit,
    edadMax: Int, onEdadMaxChanged: (Int) -> Unit,
    ciudades: List<String>, selectedCiudad: String, onCiudadSelected: (String) -> Unit,
    onClose: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Filtros", fontWeight = FontWeight.Bold, fontSize = 20.sp)

            SexoDropdown(selectedSexo, onSexoSelected)

            Text("Edad: $edadMin - $edadMax años")
            Slider(value = edadMin.toFloat(), onValueChange = { onEdadMinChanged(it.toInt()) }, valueRange = 0f..100f)
            Slider(value = edadMax.toFloat(), onValueChange = { onEdadMaxChanged(it.toInt()) }, valueRange = 0f..100f)

            CiudadDropdown(ciudades, selectedCiudad, onCiudadSelected)

            Button(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
                Text("Aplicar Filtros")
            }
        }
    }
}

@Composable
fun SexoDropdown(selectedSexo: String, onSexoSelected: (String) -> Unit) {
    val opciones = listOf("Todos", "M", "F")
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(onClick = { expanded = true }) {
            Text("Sexo: $selectedSexo")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            opciones.forEach { sexo ->
                DropdownMenuItem(
                    text = { Text(sexo) },
                    onClick = {
                        onSexoSelected(sexo)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun CiudadDropdown(ciudades: List<String>, selectedCiudad: String, onCiudadSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(onClick = { expanded = true }) {
            Text("Ciudad: $selectedCiudad")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            ciudades.forEach { ciudad ->
                DropdownMenuItem(
                    text = { Text(ciudad) },
                    onClick = {
                        onCiudadSelected(ciudad)
                        expanded = false
                    }
                )
            }
        }
    }
}
