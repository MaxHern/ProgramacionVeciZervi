package com.example.vecizervi.ui

import androidx.compose.foundation.clickable
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
fun PantallaChatInbox(
    navController: NavController,
    userRepo: UserRepository,
    repo: TrabajoRepository = TrabajoRepository() // ✅ Usamos el repositorio
) {
    val usuarioActual = userRepo.obtenerUsuarioActual()
    var misTrabajos by remember { mutableStateOf<List<Trabajo>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(usuarioActual) {
        if (usuarioActual != null) {
            scope.launch {
                try {
                    misTrabajos = repo.obtenerTrabajosPorCliente(
                        (usuarioActual.idUsuario ?: 0).toLong()
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    cargando = false
                }
            }
        } else {
            cargando = false
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Mensajes", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(12.dp))

        when {
            cargando -> CircularProgressIndicator()
            misTrabajos.isEmpty() -> Text("No tienes conversaciones activas", style = MaterialTheme.typography.bodyMedium)
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(misTrabajos) { trabajo ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate("chat/${trabajo.titulo}/${trabajo.idTrabajo}")
                                },
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(trabajo.titulo, style = MaterialTheme.typography.titleMedium)
                                Text("Estado: ${trabajo.estado}", style = MaterialTheme.typography.bodySmall)
                                Text(
                                    "Toca para ver mensajes",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
