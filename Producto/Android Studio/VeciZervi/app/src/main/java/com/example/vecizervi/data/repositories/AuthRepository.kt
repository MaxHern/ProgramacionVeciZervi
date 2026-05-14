package com.example.vecizervi.data.repositories

import com.example.vecizervi.data.repositories.UserRepository

class AuthRepository {
    private val usuarios = mutableMapOf("Iván Pérez" to "1234")
    val userRepo = UserRepository()

    fun login(nombre: String, password: String): Boolean {
        return usuarios[nombre] == password
    }

    fun register(nombre: String, password: String): Boolean {
        return if (usuarios.containsKey(nombre)) {
            false
        } else {
            usuarios[nombre] = password
            true
        }
    }
}