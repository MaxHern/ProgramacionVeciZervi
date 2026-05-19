package com.example.vecizervi.data.utils

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = "https://vecizervi-backend.onrender.com/api/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val servicioUsuarios: UsuarioService = retrofit.create(UsuarioService::class.java)
    val servicioTrabajos: TrabajoService = retrofit.create(TrabajoService::class.java)
    val servicioMensajes: MensajeService = retrofit.create(MensajeService::class.java)
    val servicioResenas:  ResenaService  = retrofit.create(ResenaService::class.java)
}