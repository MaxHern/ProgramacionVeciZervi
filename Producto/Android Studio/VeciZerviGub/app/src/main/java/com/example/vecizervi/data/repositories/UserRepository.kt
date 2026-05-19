package com.example.vecizervi.data.repositories

import com.example.vecizervi.data.models.Usuario
import com.example.vecizervi.data.models.LoginRequest
import com.example.vecizervi.data.utils.ApiClient

class UserRepository {
    private var usuarioActual: Usuario? = null

    suspend fun login(correo: String, password: String): Usuario? {
        return try {
            val response = ApiClient.servicioUsuarios.login(LoginRequest(correo, password))
            if (response.isSuccessful) {
                usuarioActual = response.body()
                usuarioActual
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun registrarUsuario(
        rut: String, nombres: String, apellidos: String,
        fechaNacimiento: String, correo: String, password: String
    ): Usuario? {
        return try {
            val nuevoUsuario = Usuario(
                rut = rut, nombres = nombres, apellidos = apellidos,
                fechaNacimiento = fechaNacimiento, correo = correo, password = password
            )
            val response = ApiClient.servicioUsuarios.registrarUsuario(nuevoUsuario)
            if (response.isSuccessful) {
                usuarioActual = response.body()
                usuarioActual
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun actualizarUsuario(
        idUsuario: Int, rut: String, nombres: String, apellidos: String,
        fechaNacimiento: String, correo: String
    ): Usuario? {
        return try {
            val usuarioEditado = Usuario(
                idUsuario = idUsuario, rut = rut, nombres = nombres,
                apellidos = apellidos, fechaNacimiento = fechaNacimiento,
                correo = correo, password = usuarioActual?.password ?: ""
            )
            val response = ApiClient.servicioUsuarios.actualizarUsuario(idUsuario, usuarioEditado)
            if (response.isSuccessful) {
                usuarioActual = response.body()
                usuarioActual
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun eliminarUsuario(idUsuario: Int): Boolean {
        return try {
            val response = ApiClient.servicioUsuarios.eliminarUsuario(idUsuario)
            if (response.isSuccessful && usuarioActual?.idUsuario == idUsuario) usuarioActual = null
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // PASO 1: pide el código → devuelve el código generado para mostrarlo en pantalla
    suspend fun recuperarPassword(correo: String): String? {
        return try {
            val response = ApiClient.servicioUsuarios.recuperarPassword(correo)
            if (response.isSuccessful) response.body()?.get("codigo") else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // PASO 2: verifica que el código ingresado sea válido
    suspend fun verificarToken(correo: String, token: String): Boolean {
        return try {
            ApiClient.servicioUsuarios.verificarToken(correo, token).isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // PASO 3: guarda la nueva contraseña en la BD
    suspend fun cambiarClave(correo: String, token: String, nuevaClave: String): Boolean {
        return try {
            ApiClient.servicioUsuarios.nuevaClave(correo, token, nuevaClave).isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun obtenerUsuarioActual(): Usuario? = usuarioActual
    fun logout() { usuarioActual = null }
}