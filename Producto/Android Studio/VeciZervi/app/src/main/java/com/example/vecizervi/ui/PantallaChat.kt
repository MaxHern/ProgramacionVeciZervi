package com.example.vecizervi.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.vecizervi.data.repositories.UserRepository

// Modelo de mensaje
data class Mensaje(val autor: String, val contenido: String)

@Composable
fun PantallaChat(navController: NavController, userRepo: UserRepository, autorNombre: String?) {
    val usuarioActual = userRepo.obtenerUsuarioActual()
    var mensajeTexto by remember { mutableStateOf("") }

    // 🔧 Mock inicial de mensajes
    val mensajes = remember {
        mutableStateListOf(
            Mensaje(autorNombre ?: "Pedro Sánchez", "Hola, ¿quieres que pase mañana?"),
            Mensaje("${usuarioActual?.nombres} ${usuarioActual?.apellidos}", "Sí, perfecto. ¿A qué hora?"),
            Mensaje(autorNombre ?: "Pedro Sánchez", "Podría ser a las 10 AM.")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Chat con ${autorNombre ?: "VeciZervi"}", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(mensajes) { mensaje ->
                val esUsuarioActual = mensaje.autor == "${usuarioActual?.nombres} ${usuarioActual?.apellidos}"

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (esUsuarioActual) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = if (esUsuarioActual) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                        tonalElevation = 2.dp,
                        modifier = Modifier.widthIn(max = 250.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            if (!esUsuarioActual) {
                                Text(mensaje.autor, style = MaterialTheme.typography.bodySmall)
                            }
                            Text(
                                mensaje.contenido,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (esUsuarioActual) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

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
                        mensajes.add(
                            Mensaje("${usuarioActual.nombres} ${usuarioActual.apellidos}", mensajeTexto)
                        )
                        mensajeTexto = ""
                    }
                }
            ) {
                Text("Enviar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}

