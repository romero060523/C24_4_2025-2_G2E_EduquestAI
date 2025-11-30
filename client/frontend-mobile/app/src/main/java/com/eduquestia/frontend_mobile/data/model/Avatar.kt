package com.eduquestia.frontend_mobile.data.model

import com.eduquestia.frontend_mobile.R
import kotlinx.serialization.Serializable

/** Avatares disponibles para seleccionar Cada avatar tiene una imagen drawable asociada */
enum class AvatarTipo(val displayName: String, val drawableRes: Int, val descripcion: String) {
    PERSONAJE_1(
            displayName = "Guerrero",
            drawableRes = R.drawable.avatar_personaje1,
            descripcion = "Un valiente guerrero listo para aprender"
    ),
    PERSONAJE_2(
            displayName = "Mago",
            drawableRes = R.drawable.avatar_personaje2,
            descripcion = "Un sabio mago sediento de conocimiento"
    ),
    PERSONAJE_3(
            displayName = "Arquero",
            drawableRes = R.drawable.avatar_personaje3,
            descripcion = "Un preciso arquero enfocado en sus metas"
    ),
    PERSONAJE_FEM(
            displayName = "Guerrera",
            drawableRes = R.drawable.avatar_personajefem,
            descripcion = "Una guerrera valiente y determinada"
    ),
    PERSONAJE_FEM_2(
            displayName = "Hechicera",
            drawableRes = R.drawable.avatar_personajefem2,
            descripcion = "Una poderosa hechicera del conocimiento"
    ),
    PERFIL_1(
            displayName = "Explorador",
            drawableRes = R.drawable.avatar_perfil1,
            descripcion = "Un explorador curioso del saber"
    ),
    AVATAR_4(
            displayName = "Elemental",
            drawableRes = R.drawable.avatar4,
            descripcion = "Un ser elemental de fuego y poder"
    ),
    AVATAR_5(
            displayName = "Caballero Oscuro",
            drawableRes = R.drawable.avatar5,
            descripcion = "Un caballero misterioso y poderoso"
    ),
    AVATAR_6(
            displayName = "Sabio Felino",
            drawableRes = R.drawable.avatar6,
            descripcion = "Un sabio con la astucia de un felino"
    );

    companion object {
        fun fromString(value: String): AvatarTipo {
            return entries.find { it.name == value } ?: PERSONAJE_1
        }
    }
}

/** Configuración de avatar del usuario */
@Serializable
data class ConfiguracionAvatar(
        val avatarSeleccionado: String = AvatarTipo.PERSONAJE_1.name,
        val avatarsDesbloqueados: List<String> =
                listOf(
                        AvatarTipo.PERSONAJE_1.name,
                        AvatarTipo.PERSONAJE_2.name,
                        AvatarTipo.PERSONAJE_3.name,
                        AvatarTipo.PERSONAJE_FEM.name,
                        AvatarTipo.PERSONAJE_FEM_2.name,
                        AvatarTipo.PERFIL_1.name,
                        AvatarTipo.AVATAR_4.name,
                        AvatarTipo.AVATAR_5.name,
                        AvatarTipo.AVATAR_6.name
                )
)
