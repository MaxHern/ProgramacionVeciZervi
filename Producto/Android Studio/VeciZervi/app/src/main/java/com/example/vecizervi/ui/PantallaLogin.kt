package com.example.vecizervi.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.vecizervi.data.repositories.UserRepository
import com.example.vecizervi.R



@Composable
fun PantallaLogin(navController: NavController, userRepo: UserRepository) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var mensajeError by remember { mutableStateOf<String?>(null) }
    var mensajeExito by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_vecizervi),
            contentDescription = "Logo VeciZervi",
            modifier = Modifier
                .height(220.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("Iniciar Sesión", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    mensajeError = "Todos los campos son obligatorios"
                    mensajeExito = null
                } else {
                    val usuario = userRepo.login(email, password)
                    if (usuario != null) {
                        mensajeExito = "Inicio de sesión exitoso"
                        mensajeError = null
                        navController.navigate("inicio")
                    } else {
                        mensajeError = "Correo o contraseña incorrectos"
                        mensajeExito = null
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ingresar")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = { navController.navigate("register") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrarme")
        }

        TextButton(
            onClick = { navController.navigate("recuperar") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("¿Olvidaste tu contraseña?")
        }

        Spacer(modifier = Modifier.height(8.dp))

        mensajeError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        mensajeExito?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
    }
}
