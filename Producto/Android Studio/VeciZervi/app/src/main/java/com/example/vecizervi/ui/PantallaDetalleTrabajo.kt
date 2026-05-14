package com.example.vecizervi.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.vecizervi.data.repositories.TrabajoRepository
import androidx.compose.ui.Alignment


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleTrabajo(
    navController: NavController,
    trabajoId: Int,
    trabajoRepo: TrabajoRepository
) {
    val trabajo = trabajoRepo.obtenerTrabajos().find { it.id == trabajoId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(trabajo?.titulo ?: "Detalle del trabajo") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (trabajo == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Trabajo no encontrado")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top
            ) {
                // 🧑 Autor y precio
                Text(trabajo.autor, style = MaterialTheme.typography.titleLarge)
                Text("Precio: $${trabajo.precio}", style = MaterialTheme.typography.bodyLarge)

                Spacer(modifier = Modifier.height(8.dp))

                // 📞 Botón de contacto
                Button(
                    onClick = {
                        navController.navigate("chat/${trabajo.autor}") // abre chat directo con el autor
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Contactar")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 🏷️ Categoría
                AssistChip(
                    onClick = { /* acción opcional */ },
                    label = { Text(trabajo.categoria) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 📄 Descripción
                Text(trabajo.descripcion, style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(16.dp))

                // 📷 Imágenes del trabajo
                if (trabajo.imagenes.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(trabajo.imagenes) { img ->
                            Image(
                                painter = painterResource(id = img),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(200.dp)
                                    .padding(4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ⭐ Reseñas (ejemplo fijo, puedes hacerlo dinámico)
                Text("Reseñas:", style = MaterialTheme.typography.titleMedium)
                Text("⭐️⭐️⭐️⭐️ Una persona muy amable - Kid Voodo")
            }
        }
    }
}
