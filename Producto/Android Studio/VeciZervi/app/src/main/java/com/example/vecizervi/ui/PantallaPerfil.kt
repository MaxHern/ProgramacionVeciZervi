package com.example.vecizervi.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.vecizervi.data.repositories.UserRepository
import com.example.vecizervi.data.models.Usuario


@Composable
fun PantallaPerfil(navController: NavController, userRepo: UserRepository) {
    val usuarioActual = userRepo.obtenerUsuarioActual()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Perfil de Usuario", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        if (usuarioActual != null) {
            Text("RUT: ${usuarioActual.rut}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Nombres: ${usuarioActual.nombres}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Apellidos: ${usuarioActual.apellidos}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Fecha de nacimiento: ${usuarioActual.fechaNacimiento}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Correo: ${usuarioActual.email}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { navController.navigate("editarPerfil") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Editar Perfil")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    userRepo.logout()
                    navController.navigate("login") {
                        popUpTo("perfil") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Cerrar Sesión")
            }
        } else {
            Text("No hay usuario activo", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("login") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar Sesión")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPantallaPerfilConUsuario() {
    val fakeNavController = rememberNavController()
    val fakeUserRepo = UserRepository().apply {
        registrarUsuario(
            rut = "12.345.678-9",
            nombres = "Iván",
            apellidos = "Pérez",
            fechaNacimiento = "2000-01-01",
            email = "ivan@mail.com",
            password = "1234" // se encripta con BCrypt
        )
    }
    PantallaPerfil(userRepo = fakeUserRepo, navController = fakeNavController)
}


