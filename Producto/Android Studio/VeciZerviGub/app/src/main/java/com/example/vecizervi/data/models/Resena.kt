package com.example.vecizervi.data.models

import com.google.gson.annotations.SerializedName

data class Resena(
    @SerializedName("id_resena")         val idResena: Long? = null,
    @SerializedName("id_trabajo")        val idTrabajo: Long = 0,
    @SerializedName("id_emisor")         val idEmisor: Long = 0,
    @SerializedName("id_receptor")       val idReceptor: Long = 0,
    @SerializedName("estrellas")         val estrellas: Int = 5,
    @SerializedName("comentario")        val comentario: String = "",
    @SerializedName("url_foto_evidencia") val urlFotoEvidencia: String? = null
)