package com.example.vecizervi.data.utils

import com.example.vecizervi.data.models.Trabajo
import com.example.vecizervi.data.models.Postulacion
import retrofit2.Response
import retrofit2.http.*

interface TrabajoService {

    @GET("trabajos")
    suspend fun getTodosLosTrabajos(): Response<List<Trabajo>>

    @GET("trabajos/disponibles")
    suspend fun getTrabajosDisponibles(): Response<List<Trabajo>>

    @GET("trabajos/{idTrabajo}")
    suspend fun getTrabajoPorId(@Path("idTrabajo") idTrabajo: Long): Response<Trabajo>

    @GET("trabajos/cliente/{idCliente}")
    suspend fun getTrabajosPorCliente(@Path("idCliente") idCliente: Long): Response<List<Trabajo>>

    @POST("trabajos/publicar")
    suspend fun postTrabajo(
        @Body trabajo: Trabajo,
        @Query("idCliente") idCliente: Long,
        @Query("idCategoria") idCategoria: Long
    ): Response<Trabajo>

    @PUT("trabajos/{idTrabajo}/editar")
    suspend fun actualizarTrabajo(
        @Path("idTrabajo") idTrabajo: Long,
        @Body trabajo: Trabajo
    ): Response<Trabajo>

    @DELETE("trabajos/{idTrabajo}")
    suspend fun eliminarTrabajo(@Path("idTrabajo") idTrabajo: Long): Response<Unit>

    @GET("postulaciones/trabajo/{idTrabajo}")
    suspend fun getPostulaciones(@Path("idTrabajo") idTrabajo: Long): Response<List<Postulacion>>

    @POST("postulaciones")
    suspend fun postularTrabajo(@Body postulacion: Postulacion): Response<Postulacion>
}