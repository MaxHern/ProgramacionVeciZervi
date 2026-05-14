package com.example.vecizervi.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.vecizervi.data.repositories.UserRepository

@Composable
fun PantallaDetalleUsuario(
    navController: NavController,
    userRepo: UserRepository,
    usuarioId: Int
) {
    val usuario = userRepo.obtenerTodosUsuarios().getOrNull(usuarioId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Detalle de Usuario", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        if (usuario != null) {
            Text("RUT: ${usuario.rut}", style = MaterialTheme.typography.bodyLarge)
            Text("Nombres: ${usuario.nombres}", style = MaterialTheme.typography.bodyLarge)
            Text("Apellidos: ${usuario.apellidos}", style = MaterialTheme.typography.bodyLarge)
            Text("Fecha de nacimiento: ${usuario.fechaNacimiento}", style = MaterialTheme.typography.bodyLarge)
            Text("Correo: ${usuario.email}", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        // ✅ Navegar a pantalla de edición
                        navController.navigate("editarPerfil/${usuarioId}")
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Editar")
                }

                OutlinedButton(
                    onClick = {
                        userRepo.eliminarUsuario(usuario.rut)
                        navController.popBackStack() // vuelve a la lista
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Eliminar")
                }
            }
        } else {
            Text("Usuario no encontrado", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver")
            }
        }
    }
}
