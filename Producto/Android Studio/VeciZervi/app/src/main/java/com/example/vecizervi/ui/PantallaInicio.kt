package com.example.vecizervi.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.vecizervi.data.repositories.TrabajoRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaInicio(navController: NavController, trabajoRepo: TrabajoRepository) {
    var searchText by remember { mutableStateOf("") }
    var selectedCategoria by remember { mutableStateOf<String?>(null) }
    var selectedComuna by remember { mutableStateOf<String?>(null) }
    var distanciaMax by remember { mutableStateOf(10f) }

    // Estados para desplegar menús
    var categoriaMenuExpanded by remember { mutableStateOf(false) }
    var comunaMenuExpanded by remember { mutableStateOf(false) }

    val categorias = listOf("Pintura", "Electricidad", "Clases", "Jardinería")
    val comunas = listOf("Maipú", "La Florida", "Santiago Centro", "Providencia")

    val trabajos = trabajoRepo.obtenerTrabajos().filter { it.estado == "Disponible" }
        .filter { it.titulo.contains(searchText, ignoreCase = true) }
        .filter { selectedCategoria == null || it.categoria == selectedCategoria }
        .filter { selectedComuna == null || it.comuna == selectedComuna }
        .filter { it.distanciaKm <= distanciaMax }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trabajos disponibles") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            // 🔎 Buscador
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Buscar trabajos") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 🎯 Filtros con menús desplegables
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Categoría
                Box {
                    Button(onClick = { categoriaMenuExpanded = true }) {
                        Text(selectedCategoria ?: "Categoría")
                    }
                    DropdownMenu(
                        expanded = categoriaMenuExpanded,
                        onDismissRequest = { categoriaMenuExpanded = false }
                    ) {
                        categorias.forEach { categoria ->
                            DropdownMenuItem(
                                text = { Text(categoria) },
                                onClick = {
                                    selectedCategoria = categoria
                                    categoriaMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                // Comuna
                Box {
                    Button(onClick = { comunaMenuExpanded = true }) {
                        Text(selectedComuna ?: "Comuna")
                    }
                    DropdownMenu(
                        expanded = comunaMenuExpanded,
                        onDismissRequest = { comunaMenuExpanded = false }
                    ) {
                        comunas.forEach { comuna ->
                            DropdownMenuItem(
                                text = { Text(comuna) },
                                onClick = {
                                    selectedComuna = comuna
                                    comunaMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 📏 Slider distancia
            Text("Distancia máxima: ${distanciaMax.toInt()} km")
            Slider(
                value = distanciaMax,
                onValueChange = { distanciaMax = it },
                valueRange = 1f..50f,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 📋 Lista de trabajos
            if (trabajos.isEmpty()) {
                Text("No hay trabajos disponibles con estos filtros")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(trabajos) { trabajo ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { /* opcional: navegar directo */ }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(trabajo.titulo, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    trabajo.descripcion,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    "Comuna: ${trabajo.comuna}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    "Precio: $${trabajo.precio}",
                                    style = MaterialTheme.typography.bodySmall
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        navController.navigate("detalleTrabajo/${trabajo.id}")
                                    }
                                ) {
                                    Text("Ver más")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

