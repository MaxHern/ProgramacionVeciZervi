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
import com.example.vecizervi.data.repositories.TrabajoRepository
import com.example.vecizervi.data.repositories.UserRepository
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPublicar(
    navController: NavController,
    trabajoRepo: TrabajoRepository,
    userRepo: UserRepository
) {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }

    val categorias = listOf("Pintura", "Electricidad", "Educación", "Jardinería", "Fletes")
    val comunas = listOf("Maipú", "La Florida", "Santiago Centro", "Providencia", "Las Condes")

    var categoria by remember { mutableStateOf(categorias.first()) }
    var comuna by remember { mutableStateOf(comunas.first()) }

    var expandedCategoria by remember { mutableStateOf(false) }
    var expandedComuna by remember { mutableStateOf(false) }

    var errorMsg by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_vecizervi),
            contentDescription = "Logo VeciZervi",
            modifier = Modifier
                .height(200.dp)
        )

        OutlinedTextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Título") })
        OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") })
        OutlinedTextField(value = precio, onValueChange = { precio = it }, label = { Text("Precio") })

        // ✅ Dropdown Categoría
        ExposedDropdownMenuBox(
            expanded = expandedCategoria,
            onExpandedChange = { expandedCategoria = !expandedCategoria }
        ) {
            OutlinedTextField(
                value = categoria,
                onValueChange = {},
                readOnly = true,
                label = { Text("Categoría") },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expandedCategoria,
                onDismissRequest = { expandedCategoria = false }
            ) {
                categorias.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            categoria = opcion
                            expandedCategoria = false
                        }
                    )
                }
            }
        }

        // ✅ Dropdown Comuna
        ExposedDropdownMenuBox(
            expanded = expandedComuna,
            onExpandedChange = { expandedComuna = !expandedComuna }
        ) {
            OutlinedTextField(
                value = comuna,
                onValueChange = {},
                readOnly = true,
                label = { Text("Comuna") },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expandedComuna,
                onDismissRequest = { expandedComuna = false }
            ) {
                comunas.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            comuna = opcion
                            expandedComuna = false
                        }
                    )
                }
            }
        }

        errorMsg?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Button(
            onClick = {
                if (titulo.isBlank() || descripcion.isBlank() || precio.isBlank()) {
                    errorMsg = "Todos los campos son obligatorios"
                } else if (precio.toIntOrNull() == null || precio.toInt() <= 0) {
                    errorMsg = "El precio debe ser un número mayor a 0"
                } else {
                    val coords = mapaComunas[comuna] ?: Pair(0.0, 0.0)
                    val fechaActual = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                    trabajoRepo.agregarTrabajo(
                        titulo = titulo,
                        descripcion = descripcion,
                        precio = precio.toInt(),
                        categoria = categoria,
                        autor = userRepo.obtenerUsuarioActual()?.nombres ?: "Anónimo",
                        comuna = comuna,
                        pago = false,
                        ubicacion = comuna,
                        fecha = fechaActual,
                        latitud = coords.first,
                        longitud = coords.second
                    )
                    navController.navigate("inicio")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Publicar")
        }
    }
}

// 🔧 Mapa de comunas → coordenadas de prueba
val mapaComunas = mapOf(
    "Maipú" to Pair(-33.5100, -70.7520),
    "La Florida" to Pair(-33.5375, -70.5790),
    "Santiago Centro" to Pair(-33.4489, -70.6693),
    "Providencia" to Pair(-33.4323, -70.6090),
    "Las Condes" to Pair(-33.4080, -70.5666)
)
