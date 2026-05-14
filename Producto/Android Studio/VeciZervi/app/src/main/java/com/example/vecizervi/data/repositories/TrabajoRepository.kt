package com.example.vecizervi.data.repositories

import com.example.vecizervi.data.models.Trabajo
import kotlin.math.*

class TrabajoRepository {

    private val trabajos = mutableListOf<Trabajo>()
    private var nextId = 1

    init {
        // ✅ Trabajos de prueba con coordenadas
        trabajos.add(
            Trabajo(
                id = nextId++,
                titulo = "Pintar reja",
                descripcion = "Necesito pintar una reja de 5 metros",
                precio = 20000,
                categoria = "Pintura",
                autor = "Juan Pérez",
                comuna = "Maipú",
                estado = "Disponible",
                pago = false,
                ubicacion = "Calle Los Olivos 123",
                fecha = "2026-05-10",
                latitud = -33.5100,
                longitud = -70.7520
            )
        )
        trabajos.add(
            Trabajo(
                id = nextId++,
                titulo = "Arreglo de enchufe",
                descripcion = "Se quemó un enchufe en la cocina",
                precio = 10000,
                categoria = "Electricidad",
                autor = "María González",
                comuna = "La Florida",
                estado = "Disponible",
                pago = false,
                ubicacion = "Av. Vicuña Mackenna 456",
                fecha = "2026-05-09",
                latitud = -33.5375,
                longitud = -70.5790
            )
        )
        trabajos.add(
            Trabajo(
                id = nextId++,
                titulo = "Clases de matemáticas",
                descripcion = "Necesito profesor para reforzar álgebra",
                precio = 15000,
                categoria = "Educación",
                autor = "Pedro Ramírez",
                comuna = "Santiago Centro",
                estado = "Disponible",
                pago = false,
                ubicacion = "Plaza de Armas",
                fecha = "2026-05-08",
                latitud = -33.4372,
                longitud = -70.6506
            )
        )
    }

    // ✅ Fórmula de Haversine para calcular distancia
    private fun calcularDistanciaKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val radioTierra = 6371.0 // km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (radioTierra * c).toFloat()
    }

    // ✅ Obtener trabajos con distancia calculada respecto al usuario
    fun obtenerTrabajos(latUsuario: Double = -33.5100, lonUsuario: Double = -70.7520): List<Trabajo> {
        return trabajos.map { trabajo ->
            val distancia = calcularDistanciaKm(latUsuario, lonUsuario, trabajo.latitud, trabajo.longitud)
            trabajo.copy(distanciaKm = distancia)
        }
    }

    fun obtenerTrabajoPorId(id: Int): Trabajo? = trabajos.find { it.id == id }

    fun agregarTrabajo(
        titulo: String,
        descripcion: String,
        precio: Int,
        categoria: String,
        autor: String,
        comuna: String,
        pago: Boolean,
        ubicacion: String,
        fecha: String,
        latitud: Double,
        longitud: Double
    ) {
        val nuevoTrabajo = Trabajo(
            id = nextId++,
            titulo = titulo,
            descripcion = descripcion,
            precio = precio,
            categoria = categoria,
            autor = autor,
            comuna = comuna,
            estado = "Disponible",
            pago = pago,
            ubicacion = ubicacion,
            fecha = fecha,
            latitud = latitud,
            longitud = longitud
        )
        trabajos.add(nuevoTrabajo)
    }

    fun eliminarTrabajo(trabajo: Trabajo): Boolean = trabajos.remove(trabajo)

    // ✅ Método para actualizar un trabajo existente
    fun actualizarTrabajo(trabajoEditado: Trabajo): Boolean {
        val index = trabajos.indexOfFirst { it.id == trabajoEditado.id }
        return if (index != -1) {
            trabajos[index] = trabajoEditado
            true
        } else {
            false
        }
    }
}
