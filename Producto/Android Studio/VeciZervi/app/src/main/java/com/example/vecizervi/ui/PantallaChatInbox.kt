package com.example.vecizervi.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.vecizervi.data.repositories.UserRepository

data class Conversacion(val autor: String, val ultimoMensaje: String)

@Composable
fun PantallaChatInbox(navController: NavController, userRepo: UserRepository) {
    val conversaciones = listOf(
        Conversacion("Pedro Sánchez", "Recojo cachureos a buen precio"),
        Conversacion("María López", "Clases de inglés disponibles"),
        Conversacion("General", "Bienvenido al chat general")
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Conversaciones recientes", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(conversaciones) { conversacion ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("chat/${conversacion.autor}")
                        },
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(conversacion.autor, style = MaterialTheme.typography.titleMedium)
                        Text(conversacion.ultimoMensaje, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
