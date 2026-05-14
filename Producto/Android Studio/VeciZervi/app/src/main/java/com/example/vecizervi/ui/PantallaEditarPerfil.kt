package com.example.vecizervi.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.vecizervi.data.repositories.UserRepository
import com.example.vecizervi.data.utils.validarRut


@Composable
fun PantallaEditarPerfil(navController: NavController, userRepo: UserRepository) {
    val usuarioActual = userRepo.obtenerUsuarioActual()

    var rut by remember { mutableStateOf(usuarioActual?.rut ?: "") }
    var nombres by remember { mutableStateOf(usuarioActual?.nombres ?: "") }
    var apellidos by remember { mutableStateOf(usuarioActual?.apellidos ?: "") }
    var fechaNacimiento by remember { mutableStateOf(usuarioActual?.fechaNacimiento ?: "") }
    var email by remember { mutableStateOf(usuarioActual?.email ?: "") }

    var mensajeError by remember { mutableStateOf<String?>(null) }
    var mensajeExito by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Editar Perfil", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = rut, onValueChange = { rut = it }, label = { Text("RUT") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = nombres, onValueChange = { nombres = it }, label = { Text("Nombres") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = apellidos, onValueChange = { apellidos = it }, label = { Text("Apellidos") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = fechaNacimiento, onValueChange = { fechaNacimiento = it }, label = { Text("Fecha de nacimiento") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Correo electrónico") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (rut.isBlank() || nombres.isBlank() || apellidos.isBlank() || fechaNacimiento.isBlank() || email.isBlank()) {
                    mensajeError = "Todos los campos son obligatorios"
                    mensajeExito = null
                } else if (!validarRut(rut)) {
                    mensajeError = "RUT inválido"
                    mensajeExito = null
                } else {
                    userRepo.actualizarUsuario(rut, nombres, apellidos, fechaNacimiento, email)
                    mensajeExito = "Perfil actualizado exitosamente"
                    mensajeError = null
                    navController.navigate("perfil") {
                        popUpTo("editarPerfil") { inclusive = true }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Cambios")
        }

        Spacer(modifier = Modifier.height(8.dp))

        mensajeError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        mensajeExito?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
    }
}
