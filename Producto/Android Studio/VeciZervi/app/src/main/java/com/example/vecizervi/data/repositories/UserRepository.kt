package com.example.vecizervi.data.repositories

import com.example.vecizervi.data.models.Usuario
import org.mindrot.jbcrypt.BCrypt

class UserRepository {
    private val usuarios = mutableListOf<Usuario>()
    private var usuarioActual: Usuario? = null

    // ✅ Registrar usuario con contraseña encriptada
    fun registrarUsuario(rut: String, nombres: String, apellidos: String, fechaNacimiento: String, email: String, password: String) {
        val hashPassword = BCrypt.hashpw(password, BCrypt.gensalt())
        val nuevoUsuario = Usuario(rut, nombres, apellidos, fechaNacimiento, email, hashPassword)
        usuarios.add(nuevoUsuario)
        usuarioActual = nuevoUsuario
    }


    // ✅ Login con verificación segura de contraseña
    fun login(email: String, password: String): Usuario? {
        val usuario = usuarios.find { it.email == email }
        return if (usuario != null && BCrypt.checkpw(password, usuario.password)) {
            usuarioActual = usuario
            usuario
        } else {
            null
        }
    }

    fun obtenerUsuarioActual(): Usuario? = usuarioActual

    fun logout() {
        usuarioActual = null
    }

    fun actualizarUsuario(rut: String, nombres: String, apellidos: String, fechaNacimiento: String, email: String) {
        usuarioActual?.let {
            it.rut = rut
            it.nombres = nombres
            it.apellidos = apellidos
            it.fechaNacimiento = fechaNacimiento
            it.email = email
        }
    }

    fun obtenerTodosUsuarios(): List<Usuario> = usuarios

    fun eliminarUsuario(rut: String) {
        usuarios.removeIf { it.rut == rut }
        if (usuarioActual?.rut == rut) {
            usuarioActual = null
        }
    }
}
