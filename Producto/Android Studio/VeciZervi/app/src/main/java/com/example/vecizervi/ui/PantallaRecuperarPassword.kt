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
fun PantallaRecuperarPassword(navController: NavController, userRepo: UserRepository) {
    var email by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ✅ Logo arriba
        Image(
            painter = painterResource(id = R.drawable.logo_vecizervi),
            contentDescription = "Logo VeciZervi",
            modifier = Modifier
                .height(220.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("Recuperar Contraseña", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.isBlank()) {
                    mensaje = "Debes ingresar tu correo"
                } else {
                    // Aquí puedes implementar la lógica real con tu UserRepository
                    // Ejemplo: userRepo.recuperarPassword(email)
                    mensaje = "Si el correo existe, recibirás un email con instrucciones"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar instrucciones")
        }

        Spacer(modifier = Modifier.height(16.dp))

        mensaje?.let { Text(it, color = MaterialTheme.colorScheme.primary) }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Volver al login")
        }
    }
}
