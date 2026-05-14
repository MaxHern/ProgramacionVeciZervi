package com.example.vecizervi.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Scaffold
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.vecizervi.data.repositories.UserRepository
import com.example.vecizervi.data.repositories.TrabajoRepository
import com.example.vecizervi.ui.components.BottomBar

@Composable
fun AppNavHost(
    navController: NavHostController,
    trabajoRepo: TrabajoRepository,
    userRepo: UserRepository
) {
    Scaffold(
        bottomBar = { BottomBar(navController) } // ✅ Barra inferior integrada
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            // ✅ Pantalla de Login
            composable("login") { PantallaLogin(navController, userRepo) }

            // ✅ Pantalla de Registro
            composable("registro") { PantallaRegister(navController, userRepo) }

            composable("inicio") { PantallaInicio(navController, trabajoRepo) }

            // ✅ Publicar nuevo trabajo
            composable("publicar") { PantallaPublicar(navController, trabajoRepo, userRepo) }

            // ✅ Perfil
            composable("perfil") { PantallaPerfil(navController, userRepo) }

            composable("recuperar") { PantallaRecuperarPassword(navController, userRepo) }

            // ✅ Detalle de trabajo con parámetro id
            composable("detalleTrabajo/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: -1
                PantallaDetalleTrabajo(navController, id, trabajoRepo)
            }

            // Chat general desde BottomBar
            composable("chat") {
                PantallaChatInbox(navController, userRepo)
            }

            composable("chat/{autorNombre}") { backStackEntry ->
                val autorNombre = backStackEntry.arguments?.getString("autorNombre")
                PantallaChat(navController, userRepo, autorNombre)
            }

            // ✅ Editar trabajo con parámetro id
            composable("editar/{trabajoId}") { backStackEntry ->
                val trabajoId = backStackEntry.arguments?.getString("trabajoId")?.toIntOrNull()
                if (trabajoId != null) {
                    PantallaEditarTrabajo(navController, trabajoId, trabajoRepo)
                }
            }
        }
    }
}
