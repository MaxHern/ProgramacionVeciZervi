package com.example.vecizervi.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.vecizervi.data.models.Trabajo
import com.example.vecizervi.data.repositories.TrabajoRepository

@Composable
fun PantallaListaTrabajos(trabajoRepo: TrabajoRepository) {
    var filtro by remember { mutableStateOf("fecha") }
    var query by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf<String?>(null) }
    var comunaSeleccionada by remember { mutableStateOf<String?>(null) }
    var distanciaMax by remember { mutableStateOf(10f) } // km

    val trabajos = remember { mutableStateListOf<Trabajo>() }
    val categorias = listOf("Jardinería", "Aseo", "Construcción", "Electricidad")
    val comunas = listOf("Maipú", "Ñuñoa", "Providencia", "Santiago Centro")

    LaunchedEffect(Unit) {
        trabajos.clear()
        trabajos.addAll(trabajoRepo.obtenerTrabajos())
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Trabajos disponibles", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(8.dp))

        // 🔍 Buscador
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Buscar trabajos") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 🏷️ Filtro de categorías
        DropdownMenuFiltro("Categoría", categorias, categoriaSeleccionada) { categoriaSeleccionada = it }

        Spacer(modifier = Modifier.height(8.dp))

        // 📍 Filtro de comunas
        DropdownMenuFiltro("Comuna", comunas, comunaSeleccionada) { comunaSeleccionada = it }

        Spacer(modifier = Modifier.height(8.dp))

        // 📏 Filtro por distancia
        Text("Distancia máxima: ${distanciaMax.toInt()} km")
        Slider(
            value = distanciaMax,
            onValueChange = { distanciaMax = it },
            valueRange = 1f..20f,
            steps = 19
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🔽 Botones de orden
        Row {
            Button(onClick = { filtro = "fecha" }) { Text("Ordenar por fecha") }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { filtro = "pago" }) { Text("Ordenar por pago") }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { filtro = "ubicacion" }) { Text("Ordenar por ubicación") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ Lista filtrada y ordenada
        val listaFiltrada = trabajos
            .filter { it.titulo.contains(query, ignoreCase = true) || it.descripcion.contains(query, ignoreCase = true) }
            .filter { categoriaSeleccionada == null || it.categoria == categoriaSeleccionada }
            .filter { comunaSeleccionada == null || it.comuna == comunaSeleccionada }
            .filter { it.distanciaKm <= distanciaMax }

        val listaOrdenada = when (filtro) {
            "pago" -> listaFiltrada.sortedByDescending { it.pago }
            "ubicacion" -> listaFiltrada.sortedBy { it.ubicacion }
            else -> listaFiltrada.sortedBy { it.fecha }
        }

        LazyColumn {
            items(listaOrdenada) { trabajo ->
                Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Título: ${trabajo.titulo}", style = MaterialTheme.typography.titleMedium)
                        Text("Descripción: ${trabajo.descripcion}")
                        Text("Precio: $${trabajo.precio}")
                        Text("Categoría: ${trabajo.categoria}")
                        Text("Comuna: ${trabajo.comuna}")
                        Text("Distancia: ${trabajo.distanciaKm} km")
                        Text("Fecha: ${trabajo.fecha}")
                    }
                }
            }
        }
    }
}

@Composable
fun DropdownMenuFiltro(
    label: String,
    opciones: List<String>,
    seleccion: String?,
    onSeleccion: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(seleccion ?: label)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onSeleccion(opcion)
                        expanded = false
                    }
                )
            }
        }
    }
}
