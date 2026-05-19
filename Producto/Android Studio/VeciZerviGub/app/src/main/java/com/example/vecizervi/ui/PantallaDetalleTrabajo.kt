package com.example.vecizervi.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.vecizervi.data.models.Postulacion
import com.example.vecizervi.data.models.Resena
import com.example.vecizervi.data.repositories.TrabajoRepository
import com.example.vecizervi.data.repositories.UserRepository
import com.example.vecizervi.data.utils.ApiClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleTrabajo(
    navController: NavController,
    trabajoId: Long,
    userRepo: UserRepository,
    trabajoRepo: TrabajoRepository = TrabajoRepository()
) {
    var cargando            by remember { mutableStateOf(true) }
    var mensajePostulacion  by remember { mutableStateOf("") }
    var mostrarDialogo      by remember { mutableStateOf(false) }
    var mensajeResultado    by remember { mutableStateOf<String?>(null) }
    val scope               = rememberCoroutineScope()
    val usuarioActual       = userRepo.obtenerUsuarioActual()
    val trabajoState        = remember { mutableStateOf<com.example.vecizervi.data.models.Trabajo?>(null) }

    // Reseñas
    var resenas             by remember { mutableStateOf<List<Resena>>(emptyList()) }
    var mostrarFormResena   by remember { mutableStateOf(false) }
    var estrellasSeleccionadas by remember { mutableIntStateOf(5) }
    var comentarioResena    by remember { mutableStateOf("") }
    var enviandoResena      by remember { mutableStateOf(false) }
    var mensajeResena       by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(trabajoId) {
        val t = trabajoRepo.obtenerTrabajoPorId(trabajoId)
        trabajoState.value = t
        cargando = false

        // Cargar reseñas
        try {
            val resp = ApiClient.servicioResenas.getResenasPorTrabajo(trabajoId)
            if (resp.isSuccessful) resenas = resp.body() ?: emptyList()
        } catch (e: Exception) { e.printStackTrace() }
    }

    // Diálogo postular
    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Postular al trabajo") },
            text = {
                OutlinedTextField(
                    value = mensajePostulacion,
                    onValueChange = { mensajePostulacion = it },
                    label = { Text("Mensaje de presentación") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (usuarioActual != null) {
                        scope.launch {
                            try {
                                val ok = trabajoRepo.postularTrabajo(
                                    trabajoId,
                                    usuarioActual.idUsuario?.toLong() ?: 0L,
                                    mensajePostulacion
                                )
                                mensajeResultado = if (ok) "¡Postulación enviada!" else "Error al postular"
                            } catch (e: Exception) {
                                mensajeResultado = "Error: ${e.message}"
                            }
                            mostrarDialogo = false
                        }
                    }
                }) { Text("Enviar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (cargando) "Cargando..." else trabajoState.value?.titulo ?: "Detalle") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            cargando -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            trabajoState.value == null -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center) {
                    Text("Trabajo no encontrado")
                }
            }
            else -> {
                val t = trabajoState.value!!
                val esElDueno = usuarioActual?.idUsuario != null &&
                        usuarioActual.idUsuario == t.cliente?.idUsuario

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Info del trabajo
                    Text(t.titulo, style = MaterialTheme.typography.headlineSmall)
                    HorizontalDivider()
                    Text("Descripción:", style = MaterialTheme.typography.labelLarge)
                    Text(t.descripcion, style = MaterialTheme.typography.bodyMedium)
                    Text("Precio: \$${t.precio}", style = MaterialTheme.typography.bodyLarge)
                    Text("Comuna: ${t.comuna}", style = MaterialTheme.typography.bodyMedium)
                    Text("Estado: ${t.estado}", style = MaterialTheme.typography.bodyMedium)
                    t.categoria?.nombreCategoria?.let {
                        Text("Categoría: $it", style = MaterialTheme.typography.bodyMedium)
                    }
                    t.cliente?.let {
                        Text("Publicado por: ${it.nombres} ${it.apellidos}",
                            style = MaterialTheme.typography.bodySmall)
                    }
                    Text("Publicado: ${t.fechaPublicacion}",
                        style = MaterialTheme.typography.bodySmall)

                    Spacer(modifier = Modifier.height(8.dp))

                    mensajeResultado?.let {
                        Text(it, color = if (it.contains("Error"))
                            MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
                    }

                    // Botón postular (no dueño)
                    if (usuarioActual != null && !esElDueno) {
                        Button(onClick = { mostrarDialogo = true },
                            modifier = Modifier.fillMaxWidth()) {
                            Text("Postularme a este trabajo")
                        }
                    }

                    // Chat
                    if (usuarioActual != null) {
                        OutlinedButton(
                            onClick = { navController.navigate("chat/${t.titulo}/${t.idTrabajo ?: 0}") },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Abrir Chat") }
                    }

                    // Editar (solo dueño)
                    if (esElDueno) {
                        Button(
                            onClick = { navController.navigate("editar/${t.idTrabajo ?: 0}") },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Editar publicación") }
                    }

                    // ── SECCIÓN RESEÑAS ──────────────────────────────────
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Reseñas", style = MaterialTheme.typography.titleMedium)

                    if (resenas.isEmpty()) {
                        Text("Aún no hay reseñas para este trabajo.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        resenas.forEach { resena ->
                            Card(modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(1.dp)) {
                                Column(modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    // Estrellas
                                    Row {
                                        repeat(5) { i ->
                                            Icon(
                                                imageVector = if (i < resena.estrellas)
                                                    Icons.Filled.Star else Icons.Outlined.StarOutline,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                    if (resena.comentario.isNotBlank()) {
                                        Text(resena.comentario,
                                            style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        }
                    }

                    // Formulario nueva reseña (solo si no eres el dueño y hay sesión)
                    if (usuarioActual != null && !esElDueno) {
                        Spacer(modifier = Modifier.height(4.dp))

                        if (!mostrarFormResena) {
                            OutlinedButton(
                                onClick = { mostrarFormResena = true },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("Dejar una reseña") }
                        } else {
                            Card(modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(2.dp)) {
                                Column(modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("Tu reseña", style = MaterialTheme.typography.titleSmall)

                                    // Selector de estrellas
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        repeat(5) { i ->
                                            IconButton(
                                                onClick = { estrellasSeleccionadas = i + 1 },
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (i < estrellasSeleccionadas)
                                                        Icons.Filled.Star else Icons.Outlined.StarOutline,
                                                    contentDescription = "${i + 1} estrellas",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(28.dp)
                                                )
                                            }
                                        }
                                        Text("$estrellasSeleccionadas/5",
                                            modifier = Modifier.align(Alignment.CenterVertically),
                                            style = MaterialTheme.typography.bodySmall)
                                    }

                                    OutlinedTextField(
                                        value = comentarioResena,
                                        onValueChange = { comentarioResena = it },
                                        label = { Text("Escribe tu comentario...") },
                                        modifier = Modifier.fillMaxWidth(),
                                        minLines = 2
                                    )

                                    mensajeResena?.let {
                                        Text(it, color = if (it.contains("Error"))
                                            MaterialTheme.colorScheme.error
                                        else MaterialTheme.colorScheme.primary,
                                            style = MaterialTheme.typography.bodySmall)
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = {
                                                scope.launch {
                                                    enviandoResena = true
                                                    try {
                                                        val nuevaResena = Resena(
                                                            idTrabajo  = trabajoId,
                                                            idEmisor   = usuarioActual.idUsuario?.toLong() ?: 0L,
                                                            idReceptor = t.cliente?.idUsuario?.toLong() ?: 0L,
                                                            estrellas  = estrellasSeleccionadas,
                                                            comentario = comentarioResena
                                                        )
                                                        val resp = ApiClient.servicioResenas.crearResena(nuevaResena)
                                                        if (resp.isSuccessful) {
                                                            mensajeResena = "¡Reseña enviada!"
                                                            resenas = resenas + (resp.body() ?: nuevaResena)
                                                            comentarioResena = ""
                                                            estrellasSeleccionadas = 5
                                                            mostrarFormResena = false
                                                        } else {
                                                            mensajeResena = "Error al enviar (${resp.code()})"
                                                        }
                                                    } catch (e: Exception) {
                                                        mensajeResena = "Error: ${e.message}"
                                                    } finally {
                                                        enviandoResena = false
                                                    }
                                                }
                                            },
                                            modifier = Modifier.weight(1f),
                                            enabled = !enviandoResena
                                        ) {
                                            if (enviandoResena)
                                                CircularProgressIndicator(modifier = Modifier.size(18.dp))
                                            else Text("Enviar")
                                        }
                                        OutlinedButton(
                                            onClick = { mostrarFormResena = false },
                                            modifier = Modifier.weight(1f)
                                        ) { Text("Cancelar") }
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