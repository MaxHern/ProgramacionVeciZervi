package com.example.vecizervi.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.vecizervi.data.models.Mensaje
import com.example.vecizervi.data.repositories.TrabajoRepository
import com.example.vecizervi.data.repositories.UserRepository
import com.example.vecizervi.data.utils.ApiClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaChat(
    navController: NavController,
    userRepo: UserRepository,
    autorNombre: String?,
    idTrabajo: Int,
    trabajoRepo: TrabajoRepository = TrabajoRepository()
) {
    val usuarioActual   = userRepo.obtenerUsuarioActual()
    var mensajeTexto    by remember { mutableStateOf("") }
    var mensajes        by remember { mutableStateOf<List<Mensaje>>(emptyList()) }
    var estadoTrabajo   by remember { mutableStateOf("Disponible") }
    var esElDueno       by remember { mutableStateOf(false) }
    var mostrarDialogoAceptar   by remember { mutableStateOf(false) }
    var mostrarDialogoCancelar  by remember { mutableStateOf(false) }
    var mensajeEstado   by remember { mutableStateOf<String?>(null) }
    val scope           = rememberCoroutineScope()
    val listState       = rememberLazyListState()

    // Cargar datos del trabajo y mensajes
    LaunchedEffect(idTrabajo) {
        // Cargar estado del trabajo
        try {
            val t = trabajoRepo.obtenerTrabajoPorId(idTrabajo.toLong())
            if (t != null) {
                estadoTrabajo = t.estado
                esElDueno = usuarioActual?.idUsuario != null &&
                        usuarioActual.idUsuario == t.cliente?.idUsuario
            }
        } catch (e: Exception) { e.printStackTrace() }

        // Polling de mensajes cada 5 segundos
        while (true) {
            try {
                val response = ApiClient.servicioMensajes.getMensajes(idTrabajo)
                if (response.isSuccessful) mensajes = response.body() ?: emptyList()
            } catch (e: Exception) { e.printStackTrace() }
            delay(5000)
        }
    }

    LaunchedEffect(mensajes.size) {
        if (mensajes.isNotEmpty()) listState.animateScrollToItem(mensajes.size - 1)
    }

    // Diálogo Aceptar trabajo
    if (mostrarDialogoAceptar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoAceptar = false },
            title = { Text("Aceptar trabajo") },
            text = { Text("¿Confirmas que quieres aceptar este trabajo? Su estado cambiará a Asignado.") },
            confirmButton = {
                Button(onClick = {
                    scope.launch {
                        try {
                            val t = trabajoRepo.obtenerTrabajoPorId(idTrabajo.toLong())
                            if (t != null) {
                                val actualizado = t.copy(estado = "Asignado")
                                val ok = trabajoRepo.actualizarTrabajo(idTrabajo.toLong(), actualizado)
                                if (ok != null) {
                                    estadoTrabajo = "Asignado"
                                    mensajeEstado = "✓ Trabajo aceptado"
                                } else {
                                    mensajeEstado = "Error al aceptar el trabajo"
                                }
                            }
                        } catch (e: Exception) {
                            mensajeEstado = "Error: ${e.message}"
                        }
                        mostrarDialogoAceptar = false
                    }
                }) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoAceptar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo Cancelar trabajo
    if (mostrarDialogoCancelar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoCancelar = false },
            title = { Text("Cancelar trabajo") },
            text = { Text("¿Estás seguro que quieres cancelar este trabajo? Su estado volverá a Disponible.") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                val t = trabajoRepo.obtenerTrabajoPorId(idTrabajo.toLong())
                                if (t != null) {
                                    val actualizado = t.copy(estado = "Disponible")
                                    val ok = trabajoRepo.actualizarTrabajo(idTrabajo.toLong(), actualizado)
                                    if (ok != null) {
                                        estadoTrabajo = "Disponible"
                                        mensajeEstado = "Trabajo cancelado"
                                    } else {
                                        mensajeEstado = "Error al cancelar el trabajo"
                                    }
                                }
                            } catch (e: Exception) {
                                mensajeEstado = "Error: ${e.message}"
                            }
                            mostrarDialogoCancelar = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoCancelar = false }) {
                    Text("No cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(autorNombre ?: "Chat")
                        Text(
                            "Estado: $estadoTrabajo",
                            style = MaterialTheme.typography.labelSmall,
                            color = when (estadoTrabajo) {
                                "Asignado"   -> MaterialTheme.colorScheme.primary
                                "Finalizado" -> MaterialTheme.colorScheme.tertiary
                                else         -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(8.dp)
        ) {
            // Botones de acción (solo para el dueño del trabajo)
            if (esElDueno) {
                mensajeEstado?.let {
                    Text(it,
                        color = if (it.contains("Error")) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 4.dp))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (estadoTrabajo == "Disponible") {
                        Button(
                            onClick = { mostrarDialogoAceptar = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) { Text("Aceptar trabajo") }
                    }
                    if (estadoTrabajo == "Asignado") {
                        Button(
                            onClick = { mostrarDialogoCancelar = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) { Text("Cancelar trabajo") }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Lista de mensajes
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(mensajes) { mensaje ->
                    val esYo = mensaje.idEmisor == usuarioActual?.idUsuario
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (esYo) Arrangement.End else Arrangement.Start
                    ) {
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = if (esYo) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    mensaje.contenido,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (esYo) MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    mensaje.fechaEnvio,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (esYo) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Input de mensaje
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = mensajeTexto,
                    onValueChange = { mensajeTexto = it },
                    label = { Text("Escribe un mensaje...") },
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        if (mensajeTexto.isNotBlank() && usuarioActual != null) {
                            val texto = mensajeTexto.trim()
                            mensajeTexto = ""
                            scope.launch {
                                try {
                                    val fechaActual = SimpleDateFormat(
                                        "yyyy-MM-dd HH:mm:ss", Locale.getDefault()
                                    ).format(Date())
                                    val nuevoMensaje = Mensaje(
                                        idTrabajo  = idTrabajo,
                                        idEmisor   = usuarioActual.idUsuario ?: 0,
                                        idReceptor = 0,
                                        contenido  = texto,
                                        fechaEnvio = fechaActual
                                    )
                                    val response = ApiClient.servicioMensajes.postMensaje(nuevoMensaje)
                                    if (response.isSuccessful) {
                                        mensajes = mensajes + (response.body() ?: nuevoMensaje)
                                    }
                                } catch (e: Exception) { e.printStackTrace() }
                            }
                        }
                    }
                ) { Text("Enviar") }
            }
        }

    }
}