package com.example.vecizervi.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.vecizervi.data.repositories.UserRepository
import com.example.vecizervi.data.utils.validarRut
import com.example.vecizervi.data.utils.validarPassword
import com.example.vecizervi.data.utils.calcularEdad

@Composable
fun PantallaRegister(navController: NavController, userRepo: UserRepository) {
    var rut by remember { mutableStateOf("") }
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") } // formato yyyy-MM-dd
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var mensajeError by remember { mutableStateOf<String?>(null) }
    var mensajeExito by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Registro de Usuario", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = rut, onValueChange = { rut = it }, label = { Text("RUT") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = nombres, onValueChange = { nombres = it }, label = { Text("Nombres") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = apellidos, onValueChange = { apellidos = it }, label = { Text("Apellidos") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = fechaNacimiento, onValueChange = { fechaNacimiento = it }, label = { Text("Fecha de nacimiento (yyyy-MM-dd)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Correo electrónico") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (rut.isBlank() || nombres.isBlank() || apellidos.isBlank() || fechaNacimiento.isBlank() || email.isBlank() || password.isBlank()) {
                    mensajeError = "Todos los campos son obligatorios"
                    mensajeExito = null
                } else if (!validarRut(rut)) {
                    mensajeError = "RUT inválido"
                    mensajeExito = null
                } else if (!validarPassword(password)) {
                    mensajeError = "La contraseña debe tener mínimo 8 caracteres, una mayúscula, un número y un símbolo"
                    mensajeExito = null
                } else {
                    val edad = calcularEdad(fechaNacimiento)
                    if (edad < 18) {
                        mensajeError = "Debes ser mayor de edad para registrarte"
                        mensajeExito = null
                    } else {
                        userRepo.registrarUsuario(rut, nombres, apellidos, fechaNacimiento, email, password)
                        mensajeExito = "Registro exitoso, identidad validada"
                        mensajeError = null
                        navController.navigate("login")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrarse")
        }

        Spacer(modifier = Modifier.height(8.dp))

        mensajeError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        mensajeExito?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
    }
}
