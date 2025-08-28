package com.ajterrassa.validaciofacturesalbarans.navigation

object Rutes {
    object LlistaAlbarans {

    }

    const val Splash = "splash"
    const val Login = "login"
    const val Inici = "home"
    const val NouAlbara = "nou-albara"
    const val Pendents = "pendents"
    const val PendentPreview = "pendent-preview/{id}"
    const val LlistaAlbaransEnviats = "llista-albarans"
    fun pendentPreview(id: Long) = "pendent-preview/$id"
}
