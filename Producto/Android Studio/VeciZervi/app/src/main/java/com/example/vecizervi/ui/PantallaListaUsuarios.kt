package com.example.vecizervi.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.vecizervi.data.repositories.UserRepository

@Composable
fun PantallaListaUsuarios(navController: NavController, userRepo: UserRepository) {
    val usuarios = userRepo.obtenerTodosUsuarios()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Lista de Usuarios", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(usuarios) { usuario ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("RUT: ${usuario.rut}", style = MaterialTheme.typography.bodyMedium)
                        Text("Nombres: ${usuario.nombres}", style = MaterialTheme.typography.bodyMedium)
                        Text("Apellidos: ${usuario.apellidos}", style = MaterialTheme.typography.bodyMedium)
                        Text("Fecha de nacimiento: ${usuario.fechaNacimiento}", style = MaterialTheme.typography.bodyMedium)
                        Text("Correo: ${usuario.email}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
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
