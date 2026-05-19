package com.example.vecizervi.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.vecizervi.data.models.Trabajo
import com.example.vecizervi.data.repositories.UserRepository
import com.example.vecizervi.data.repositories.TrabajoRepository
import kotlinx.coroutines.launch

@Composable
fun PantallaPerfil(
    navController: NavController,
    userRepo: UserRepository,
    repo: TrabajoRepository = TrabajoRepository() // ✅ Usamos el repositorio
) {
    val usuarioActual = userRepo.obtenerUsuarioActual()
    var misTrabajos by remember { mutableStateOf<List<Trabajo>>(emptyList()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(usuarioActual) {
        if (usuarioActual != null) {
            try {
                misTrabajos = repo.obtenerTrabajosPorCliente(
                    (usuarioActual.idUsuario ?: 0).toLong()
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Perfil de Usuario", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        if (usuarioActual != null) {
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("RUT: ${usuarioActual.rut}", style = MaterialTheme.typography.bodyMedium)
                    Text("Nombres: ${usuarioActual.nombres}", style = MaterialTheme.typography.bodyMedium)
                    Text("Apellidos: ${usuarioActual.apellidos}", style = MaterialTheme.typography.bodyMedium)
                    Text("Fecha de nacimiento: ${usuarioActual.fechaNacimiento}", style = MaterialTheme.typography.bodyMedium)
                    Text("Correo: ${usuarioActual.correo}", style = MaterialTheme.typography.bodyMedium)
                    Text("Calificación: ★ ${usuarioActual.calificacionPromedio}", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { navController.navigate("editarPerfil") }, modifier = Modifier.fillMaxWidth()) {
                Text("Editar Perfil")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    userRepo.logout()
                    navController.navigate("login") { popUpTo(0) { inclusive = true } }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Cerrar Sesión")
            }

            if (misTrabajos.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Mis publicaciones", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(misTrabajos) { trabajo ->
                        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(1.dp)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(trabajo.titulo, style = MaterialTheme.typography.titleSmall)
                                Text("Estado: ${trabajo.estado}", style = MaterialTheme.typography.bodySmall)
                                Text("Precio: ${trabajo.precio}", style = MaterialTheme.typography.bodySmall)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(onClick = { navController.navigate("editar/${trabajo.idTrabajo ?: 0}") }) {
                                        Text("Editar")
                                    }
                                    OutlinedButton(onClick = {
                                        scope.launch {
                                            val id = trabajo.idTrabajo ?: return@launch
                                            val ok = repo.eliminarTrabajo(id)
                                            if (ok) {
                                                misTrabajos = misTrabajos.filter { it.idTrabajo != id }
                                            }
                                        }
                                    }) {
                                        Text("Eliminar")
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } else {
            Text("No hay usuario activo", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate("login") }, modifier = Modifier.fillMaxWidth()) {
                Text("Iniciar Sesión")
            }
        }
    }
}
