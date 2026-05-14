package com.example.vecizervi.data.models

data class Trabajo(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val precio: Int,
    val categoria: String,
    val autor: String,
    val comuna: String,
    val estado: String,
    val pago: Boolean,
    val ubicacion: String,
    val fecha: String,
    val latitud: Double,
    val longitud: Double,
    val distanciaKm: Float = 0f, // ✅ coma aquí
    val imagenes: List<Int> = emptyList() // ids de drawables
)
