package com.example.vecizervi.data.utils

import java.util.Calendar

fun calcularEdad(fechaNacimiento: String): Int {
    // formato esperado: yyyy-MM-dd
    val partes = fechaNacimiento.split("-")
    val year = partes[0].toInt()
    val month = partes[1].toInt()
    val day = partes[2].toInt()

    val hoy = Calendar.getInstance()
    val nacimiento = Calendar.getInstance()
    nacimiento.set(year, month - 1, day) // Calendar usa meses 0-11

    var edad = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR)

    // Ajuste si aún no cumple años este año
    if (hoy.get(Calendar.DAY_OF_YEAR) < nacimiento.get(Calendar.DAY_OF_YEAR)) {
        edad--
    }

    return edad
}
