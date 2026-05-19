package com.example.vecizervi.data.repositories

import com.example.vecizervi.data.models.Usuario
import com.example.vecizervi.data.models.LoginRequest
import com.example.vecizervi.data.utils.ApiClient
import retrofit2.Response

class AuthRepository {
    private val servicioUsuarios = ApiClient.servicioUsuarios

    suspend fun registrarUsuario(usuario: Usuario): Response<Usuario> {
        return servicioUsuarios.registrarUsuario(usuario)
    }

    suspend fun login(loginRequest: LoginRequest): Response<Usuario> {
        return servicioUsuarios.login(loginRequest)
    }
}
