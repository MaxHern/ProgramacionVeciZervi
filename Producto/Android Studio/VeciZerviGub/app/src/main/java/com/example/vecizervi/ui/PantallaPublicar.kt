package com.example.vecizervi.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.vecizervi.R
import com.example.vecizervi.data.models.Trabajo
import com.example.vecizervi.data.repositories.UserRepository
import com.example.vecizervi.data.utils.ApiClient
import kotlinx.coroutines.launch

val mapaCategorias = mapOf(
    "Fletes" to 1,
    "Aseo doméstico" to 2,
    "Jardinería" to 3,
    "Gasfitería" to 4,
    "Electricidad" to 5,
    "Cuidado de mascotas" to 6,
    "Carpintero" to 7,
    "Pintor" to 8,
    "Limpieza" to 9,
    "Seguridad" to 10,
    "Tecnología" to 11
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPublicar(navController: NavController, userRepo: UserRepository) {
    val usuarioActual = userRepo.obtenerUsuarioActual()
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }

    val categorias = mapaCategorias.keys.toList()
    val comunas = listOf("Maipú", "La Florida", "Santiago Centro", "Providencia", "Las Condes", "Ñuñoa", "Puente Alto")

    var categoriaSeleccionada by remember { mutableStateOf(categorias.first()) }
    var comunaSeleccionada by remember { mutableStateOf(comunas.first()) }

    var expandedCategoria by remember { mutableStateOf(false) }
    var expandedComuna by remember { mutableStateOf(false) }

    var errorMsg by remember { mutableStateOf<String?>(null) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    var cargando by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_vecizervi),
            contentDescription = "Logo VeciZervi",
            modifier = Modifier.height(140.dp)
        )

        Text("Publicar Trabajo", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        OutlinedTextField(
            value = precio,
            onValueChange = { precio = it },
            label = { Text("Precio (\$)") },
            modifier = Modifier.fillMaxWidth()
        )

        ExposedDropdownMenuBox(
            expanded = expandedCategoria,
            onExpandedChange = { expandedCategoria = !expandedCategoria }
        ) {
            OutlinedTextField(
                value = categoriaSeleccionada,
                onValueChange = {},
                readOnly = true,
                label = { Text("Categoría") },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expandedCategoria, onDismissRequest = { expandedCategoria = false }) {
                categorias.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = { categoriaSeleccionada = opcion; expandedCategoria = false }
                    )
                }
            }
        }

        ExposedDropdownMenuBox(
            expanded = expandedComuna,
            onExpandedChange = { expandedComuna = !expandedComuna }
        ) {
            OutlinedTextField(
                value = comunaSeleccionada,
                onValueChange = {},
                readOnly = true,
                label = { Text("Comuna") },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expandedComuna, onDismissRequest = { expandedComuna = false }) {
                comunas.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = { comunaSeleccionada = opcion; expandedComuna = false }
                    )
                }
            }
        }

        errorMsg?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        mensaje?.let { Text(it, color = MaterialTheme.colorScheme.primary) }

        Button(
            onClick = {
                when {
                    usuarioActual == null -> errorMsg = "Debes iniciar sesión para publicar"
                    titulo.isBlank() || descripcion.isBlank() || precio.isBlank() -> errorMsg = "Todos los campos son obligatorios"
                    precio.toDoubleOrNull() == null || precio.toDouble() <= 0 -> errorMsg = "El precio debe ser un número mayor a 0"
                    titulo.length < 5 -> errorMsg = "El título debe ser más descriptivo"
                    else -> {
                        scope.launch {
                            cargando = true
                            try {
                                val nuevoTrabajo = Trabajo(
                                    titulo = titulo,
                                    descripcion = descripcion,
                                    comuna = comunaSeleccionada,
                                    precio = precio.toDouble(),
                                    estado = "Disponible"
                                )

                                val response = ApiClient.servicioTrabajos.postTrabajo(
                                    nuevoTrabajo,
                                    (usuarioActual.idUsuario ?: 0).toLong(),
                                    (mapaCategorias[categoriaSeleccionada] ?: 1).toLong()
                                )

                                if (response.isSuccessful) {
                                    mensaje = "¡Trabajo publicado con éxito!"
                                    errorMsg = null
                                    navController.navigate("inicio") {
                                        popUpTo("publicar") { inclusive = true }
                                    }
                                } else {
                                    errorMsg = "Error al publicar: ${response.code()}"
                                }
                            } catch (e: Exception) {
                                errorMsg = "Error de conexión: ${e.message}"
                            } finally {
                                cargando = false
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !cargando
        ) {
            if (cargando) CircularProgressIndicator(modifier = Modifier.size(20.dp))
            else Text("Publicar")
        }
    }
}