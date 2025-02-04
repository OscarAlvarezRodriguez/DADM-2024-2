package com.example.helloworld.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.*
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.navigation.NavController
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import kotlinx.coroutines.*
import org.json.JSONArray
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.net.URL

@Composable
fun MapScreen(navController: NavController) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var userLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var radius by remember { mutableStateOf(5) } // Radio de búsqueda en kilómetros
    var hasPermission by remember { mutableStateOf(false) }
    var showRationale by remember { mutableStateOf(false) }
    var locationLoading by remember { mutableStateOf(true) }
    var nearbyPlaces by remember { mutableStateOf(emptyList<GeoPoint>()) }

    // Recordar el mapa para que no se reinicie en cada recomposición
    val mapView = remember { MapView(context) }

    // Solicitar permiso de ubicación
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (!isGranted) {
            showRationale = true
            Toast.makeText(context, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
        }
    }

    // Verificar permisos y obtener ubicación actual al iniciar
    LaunchedEffect(Unit) {
        // Configurar User-Agent para evitar bloqueos en OpenStreetMap
        Configuration.getInstance().userAgentValue = context.packageName

        val permissionStatus = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permissionStatus == PermissionChecker.PERMISSION_GRANTED) {
            hasPermission = true
            locationLoading = true
            requestCurrentLocation(fusedLocationClient) { geoPoint ->
                userLocation = geoPoint
                locationLoading = false
                val userMarker = Marker(mapView)
                userMarker.position = userLocation
                userMarker.title = "Tu ubicación actual"
                mapView.overlays.add(userMarker)
            }
        } else {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Actualizar el mapa cuando cambien los lugares cercanos
    LaunchedEffect(nearbyPlaces) {
        if (userLocation != null) {
            mapView.overlays.clear()

            // Añadir marcador de ubicación actual
            val userMarker = Marker(mapView)
            userMarker.position = userLocation
            userMarker.title = "Tu ubicación actual"
            mapView.overlays.add(userMarker)

            // Añadir marcadores de puntos de interés
            nearbyPlaces.forEach { place ->
                val marker = Marker(mapView)
                marker.position = place
                marker.title = "Lugar de interés"
                mapView.overlays.add(marker)
            }

            mapView.invalidate() // Redibujar el mapa para actualizar los marcadores
        }
    }

    // Buscar puntos de interés cercanos
    fun fetchNearbyPlaces() {
        if (userLocation == null) {
            Toast.makeText(context, "Ubicación no disponible", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "https://overpass-api.de/api/interpreter?data=[out:json];node(around:${radius * 1000},${userLocation!!.latitude},${userLocation!!.longitude})[amenity];out;"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = URL(url).readText()
                val jsonObject = org.json.JSONObject(response) // Extraer el objeto principal
                val jsonArray = jsonObject.getJSONArray("elements") // Extraer el array "elements"
                val places = mutableListOf<GeoPoint>()

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    if (obj.has("lat") && obj.has("lon")) {
                        val lat = obj.getDouble("lat")
                        val lon = obj.getDouble("lon")
                        places.add(GeoPoint(lat, lon))
                    }
                }

                withContext(Dispatchers.Main) {
                    nearbyPlaces = places
                    Toast.makeText(context, "Se encontraron ${places.size} lugares", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("MapScreen", "Error obteniendo lugares: ${e.message}")
            }
        }
    }


    // Interfaz de usuario
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Box(modifier = Modifier.weight(1f)) {
            if (hasPermission && userLocation != null) {
                AndroidView(factory = { mapView.apply {
                    setMultiTouchControls(true)
                    controller.setZoom(15.0)
                    controller.setCenter(userLocation)
                } })
            } else if (!hasPermission) {
                Text(
                    text = "Permiso de ubicación denegado. Por favor, otorga permiso para ver el mapa.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (locationLoading) {
                Text(
                    text = "Obteniendo tu ubicación...",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        if (showRationale) {
            Text(
                text = "Para acceder al mapa, activa el permiso de ubicación en la configuración del dispositivo.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Radio de búsqueda: $radius km", style = MaterialTheme.typography.bodyLarge)
        Slider(
            value = radius.toFloat(),
            onValueChange = { radius = it.toInt() },
            valueRange = 1f..50f,
            steps = 49
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { fetchNearbyPlaces() }) {
            Text("Buscar puntos de interés")
        }
    }
}

@SuppressLint("MissingPermission")
fun requestCurrentLocation(
    fusedLocationClient: FusedLocationProviderClient,
    onLocationReceived: (GeoPoint?) -> Unit
) {
    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
        if (location != null) {
            onLocationReceived(GeoPoint(location.latitude, location.longitude))
        } else {
            fusedLocationClient.getCurrentLocation(
                LocationRequest.PRIORITY_HIGH_ACCURACY,
                object : CancellationToken() {
                    override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token
                    override fun isCancellationRequested(): Boolean = false
                }
            ).addOnSuccessListener { newLocation: Location? ->
                onLocationReceived(newLocation?.let { GeoPoint(it.latitude, it.longitude) })
            }
        }
    }
}
