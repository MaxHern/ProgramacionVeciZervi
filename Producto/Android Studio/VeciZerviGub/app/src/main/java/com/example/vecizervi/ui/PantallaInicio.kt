package com.example.vecizervi.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.vecizervi.data.models.Trabajo
import com.example.vecizervi.data.repositories.UserRepository
import com.example.vecizervi.data.utils.ApiClient
import kotlinx.coroutines.launch
import kotlin.math.*

fun calcularDistanciaKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
    return r * 2 * atan2(sqrt(a), sqrt(1 - a))
}

private const val LAT_REF = -33.4489
private const val LON_REF = -70.6693

// ← Coordenadas reales por comuna
private val COORDENADAS_COMUNAS = mapOf(
    "Maipú"           to Pair(-33.5122, -70.7653),
    "La Florida"      to Pair(-33.5167, -70.5833),
    "Santiago Centro" to Pair(-33.4489, -70.6693),
    "Providencia"     to Pair(-33.4333, -70.6167),
    "Las Condes"      to Pair(-33.4167, -70.5833),
    "Ñuñoa"           to Pair(-33.4500, -70.6000),
    "Puente Alto"     to Pair(-33.6000, -70.5833)
)

private val CATEGORIAS = listOf(
    "Todas", "Fletes", "Aseo doméstico", "Jardinería",
    "Gasfitería", "Electricidad", "Cuidado de mascotas",
    "Carpintero", "Pintor", "Limpieza", "Seguridad", "Tecnología"
)

private val COMUNAS = listOf(
    "Todas", "Maipú", "La Florida", "Santiago Centro",
    "Providencia", "Las Condes", "Ñuñoa", "Puente Alto"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaInicio(navController: NavController, userRepo: UserRepository) {
    var searchText            by remember { mutableStateOf("") }
    var selectedComuna        by remember { mutableStateOf("Todas") }
    var selectedCategoria     by remember { mutableStateOf("Todas") }
    var comunaMenuExpanded    by remember { mutableStateOf(false) }
    var categoriaMenuExpanded by remember { mutableStateOf(false) }
    var kmMaximo              by remember { mutableStateOf(50f) }

    var trabajos by remember { mutableStateOf<List<Trabajo>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val scope    = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = ApiClient.servicioTrabajos.getTrabajosDisponibles()
                if (response.isSuccessful) {
                    trabajos = response.body() ?: emptyList()
                } else {
                    errorMsg = "Error al cargar trabajos: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMsg = "No se pudo conectar al servidor"
                e.printStackTrace()
            } finally {
                cargando = false
            }
        }
    }

    val trabajosFiltrados = trabajos
        .filter { it.estado == "Disponible" }
        .filter {
            it.titulo.contains(searchText, ignoreCase = true) ||
                    it.descripcion.contains(searchText, ignoreCase = true)
        }
        .filter { selectedComuna == "Todas" || it.comuna == selectedComuna }
        .filter {
            selectedCategoria == "Todas" ||
                    it.categoria?.nombreCategoria?.equals(selectedCategoria, ignoreCase = true) == true
        }
        // ← Filtro de distancia usando coordenadas por comuna
        .filter {
            val coords = COORDENADAS_COMUNAS[it.comuna]
            if (coords != null)
                calcularDistanciaKm(LAT_REF, LON_REF, coords.first, coords.second) <= kmMaximo
            else true
        }
        // ← Ordenar por distancia de más cerca a más lejos
        .sortedBy {
            val coords = COORDENADAS_COMUNAS[it.comuna]
            if (coords != null)
                calcularDistanciaKm(LAT_REF, LON_REF, coords.first, coords.second)
            else Double.MAX_VALUE
        }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Trabajos disponibles") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Busca Trabajos Cercanos") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.large
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { categoriaMenuExpanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            if (selectedCategoria == "Todas") "Categoría ▾" else "$selectedCategoria ▾",
                            maxLines = 1
                        )
                    }
                    DropdownMenu(
                        expanded = categoriaMenuExpanded,
                        onDismissRequest = { categoriaMenuExpanded = false }
                    ) {
                        CATEGORIAS.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = { selectedCategoria = cat; categoriaMenuExpanded = false }
                            )
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { comunaMenuExpanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            if (selectedComuna == "Todas") "Comuna ▾" else "$selectedComuna ▾",
                            maxLines = 1
                        )
                    }
                    DropdownMenu(
                        expanded = comunaMenuExpanded,
                        onDismissRequest = { comunaMenuExpanded = false }
                    ) {
                        COMUNAS.forEach { comuna ->
                            DropdownMenuItem(
                                text = { Text(comuna) },
                                onClick = { selectedComuna = comuna; comunaMenuExpanded = false }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Distancia máxima:", style = MaterialTheme.typography.labelMedium)
                Text(
                    "${kmMaximo.toInt()} km",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Slider(
                value = kmMaximo,
                onValueChange = { kmMaximo = it },
                valueRange = 1f..50f,
                steps = 48,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (!cargando && errorMsg == null) {
                Text(
                    "${trabajosFiltrados.size} trabajo(s) encontrado(s)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            when {
                cargando -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }
                errorMsg != null -> {
                    Text(errorMsg!!, color = MaterialTheme.colorScheme.error)
                }
                trabajosFiltrados.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No hay trabajos disponibles\ncon estos filtros",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(trabajosFiltrados) { trabajo ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { navController.navigate("detalleTrabajo/${trabajo.idTrabajo}") },
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                trabajo.titulo,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            trabajo.cliente?.let {
                                                Text(
                                                    "° ${it.nombres} ${it.apellidos}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                        Text(
                                            "$${"%,.0f".format(trabajo.precio)}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        trabajo.descripcion,
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 2
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Mostrar distancia aproximada
                                    val coords = COORDENADAS_COMUNAS[trabajo.comuna]
                                    val distancia = coords?.let {
                                        calcularDistanciaKm(LAT_REF, LON_REF, it.first, it.second)
                                    }

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        trabajo.categoria?.nombreCategoria?.let { cat ->
                                            Surface(
                                                shape = MaterialTheme.shapes.small,
                                                color = MaterialTheme.colorScheme.secondaryContainer
                                            ) {
                                                Text(
                                                    cat,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                                )
                                            }
                                        }
                                        distancia?.let {
                                            Surface(
                                                shape = MaterialTheme.shapes.small,
                                                color = MaterialTheme.colorScheme.tertiaryContainer
                                            ) {
                                                Text(
                                                    "~${it.toInt()} km",
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.weight(1f))
                                        Button(
                                            onClick = { navController.navigate("detalleTrabajo/${trabajo.idTrabajo}") },
                                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                                        ) {
                                            Text("Ver Más", style = MaterialTheme.typography.labelMedium)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}