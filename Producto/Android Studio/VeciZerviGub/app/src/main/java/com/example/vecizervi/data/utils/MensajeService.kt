package com.example.vecizervi.data.utils

import com.example.vecizervi.data.models.Mensaje
import retrofit2.Response
import retrofit2.http.*

interface MensajeService {

    @GET("mensajes/{idTrabajo}")
    suspend fun getMensajes(@Path("idTrabajo") idTrabajo: Int): Response<List<Mensaje>>

    @POST("mensajes")
    suspend fun postMensaje(@Body mensaje: Mensaje): Response<Mensaje>
}
