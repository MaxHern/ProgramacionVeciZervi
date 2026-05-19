package com.example.vecizervi.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.vecizervi.R
import com.example.vecizervi.data.repositories.UserRepository
import kotlinx.coroutines.launch

@Composable
fun PantallaLogin(navController: NavController, userRepo: UserRepository) {
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf<String?>(null) }
    var cargando by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_vecizervi),
            contentDescription = "Logo VeciZervi",
            modifier = Modifier.height(220.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("Iniciar Sesión", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        mensajeError?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                when {
                    correo.isBlank() || password.isBlank() -> mensajeError = "Todos los campos son obligatorios"
                    else -> {
                        scope.launch {
                            cargando = true
                            mensajeError = null
                            val usuario = userRepo.login(correo.trim(), password)
                            if (usuario != null) {
                                navController.navigate("inicio") { popUpTo("login") { inclusive = true } }
                            } else {
                                mensajeError = "Correo o contraseña incorrectos"
                            }
                            cargando = false
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !cargando
        ) {
            if (cargando) CircularProgressIndicator(modifier = Modifier.size(20.dp))
            else Text("Ingresar")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = { navController.navigate("register") }, modifier = Modifier.fillMaxWidth()) {
            Text("Registrarme")
        }

        TextButton(onClick = { navController.navigate("recuperar") }, modifier = Modifier.fillMaxWidth()) {
            Text("¿Olvidaste tu contraseña?")
        }
    }
}
