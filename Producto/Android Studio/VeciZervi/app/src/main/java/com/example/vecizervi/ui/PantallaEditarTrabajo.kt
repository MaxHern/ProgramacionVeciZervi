package com.example.vecizervi.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.vecizervi.data.repositories.TrabajoRepository

@Composable
fun PantallaEditarTrabajo(
    navController: NavController,
    trabajoId: Int,
    trabajoRepo: TrabajoRepository
) {
    val trabajo = trabajoRepo.obtenerTrabajoPorId(trabajoId)

    if (trabajo == null) {
        Text("Trabajo no encontrado")
        return
    }

    // ✅ Estados precargados con los datos actuales
    var titulo by remember { mutableStateOf(trabajo.titulo) }
    var descripcion by remember { mutableStateOf(trabajo.descripcion) }
    var precio by remember { mutableStateOf(trabajo.precio.toString()) }
    var categoria by remember { mutableStateOf(trabajo.categoria) }
    var comuna by remember { mutableStateOf(trabajo.comuna) }
    var ubicacion by remember { mutableStateOf(trabajo.ubicacion) }
    var fecha by remember { mutableStateOf(trabajo.fecha) }
    var latitud by remember { mutableStateOf(trabajo.latitud.toString()) }
    var longitud by remember { mutableStateOf(trabajo.longitud.toString()) }

    var mensajeError by remember { mutableStateOf<String?>(null) }
    var mensajeExito by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Editar Publicación", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = precio, onValueChange = { precio = it }, label = { Text("Precio") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = categoria, onValueChange = { categoria = it }, label = { Text("Categoría") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = comuna, onValueChange = { comuna = it }, label = { Text("Comuna") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = ubicacion, onValueChange = { ubicacion = it }, label = { Text("Ubicación") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = fecha, onValueChange = { fecha = it }, label = { Text("Fecha (yyyy-MM-dd)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = latitud, onValueChange = { latitud = it }, label = { Text("Latitud") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = longitud, onValueChange = { longitud = it }, label = { Text("Longitud") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ Botón Guardar cambios
        Button(
            onClick = {
                val precioInt = precio.toIntOrNull()
                val lat = latitud.toDoubleOrNull()
                val lon = longitud.toDoubleOrNull()

                if (precioInt == null || precioInt <= 0) {
                    mensajeError = "El precio debe ser un número mayor a 0"
                    mensajeExito = null
                } else if (titulo.length < 5) {
                    mensajeError = "El título debe ser más descriptivo"
                    mensajeExito = null
                } else if (lat == null || lon == null) {
                    mensajeError = "Latitud y longitud deben ser números válidos"
                    mensajeExito = null
                } else {
                    val trabajoEditado = trabajo.copy(
                        titulo = titulo,
                        descripcion = descripcion,
                        precio = precioInt,
                        categoria = categoria,
                        comuna = comuna,
                        ubicacion = ubicacion,
                        fecha = fecha,
                        pago = false,
                        latitud = lat,
                        longitud = lon
                    )
                    // ✅ Actualizamos directamente en el repositorio
                    trabajoRepo.actualizarTrabajo(trabajoEditado)

                    mensajeExito = "¡Los cambios se guardaron exitosamente!"
                    mensajeError = null
                    navController.navigate("inicio") {
                        popUpTo("editar/$trabajoId") { inclusive = true }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar cambios")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ✅ Botón Cancelar
        TextButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancelar")
        }

        Spacer(modifier = Modifier.height(8.dp))

        mensajeError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        mensajeExito?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
    }
}
