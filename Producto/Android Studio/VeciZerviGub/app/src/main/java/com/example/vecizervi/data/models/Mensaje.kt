package com.example.vecizervi.data.models

import com.google.gson.annotations.SerializedName

data class Mensaje(
    @SerializedName("id_mensaje") val idMensaje: Int = 0,
    @SerializedName("id_trabajo") val idTrabajo: Int = 0,
    @SerializedName("id_emisor") val idEmisor: Int = 0,
    @SerializedName("id_receptor") val idReceptor: Int = 0,
    @SerializedName("contenido") val contenido: String = "",
    @SerializedName("fecha_envio") val fechaEnvio: String = ""
)
