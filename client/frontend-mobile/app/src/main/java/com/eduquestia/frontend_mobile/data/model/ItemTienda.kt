package com.eduquestia.frontend_mobile.data.model

import com.eduquestia.frontend_mobile.R
import kotlinx.serialization.Serializable

/** Categorías de items en la tienda */
enum class CategoriaItem {
    AVATAR,
    ACCESORIO,
    BONUS,
    ESPECIAL
}

/** Item disponible en la tienda */
@Serializable
data class ItemTienda(
        val id: String,
        val nombre: String,
        val descripcion: String,
        val precio: Int,
        val categoria: String,
        val imagenRes: Int? = null,
        val imagenUrl: String? = null,
        val disponible: Boolean = true,
        val comprado: Boolean = false,
        val beneficio: String? = null // Ej: "+10% XP por 1 hora"
)

/** Items predefinidos de la tienda usando los assets locales */
object ItemsTiendaDefault {

    fun obtenerItems(): List<ItemTienda> =
            listOf(
                    ItemTienda(
                            id = "item_1",
                            nombre = "Espada del Conocimiento",
                            descripcion = "Una espada legendaria forjada con sabiduría",
                            precio = 100,
                            categoria = CategoriaItem.ACCESORIO.name,
                            imagenRes = R.drawable.item_tienda1,
                            beneficio = "+5% XP en misiones"
                    ),
                    ItemTienda(
                            id = "item_2",
                            nombre = "Armadura de Guerrero",
                            descripcion = "Protección completa para el estudiante valiente",
                            precio = 150,
                            categoria = CategoriaItem.ACCESORIO.name,
                            imagenRes = R.drawable.item_tienda2,
                            beneficio = "+10% monedas en evaluaciones"
                    ),
                    ItemTienda(
                            id = "item_3",
                            nombre = "Casco del Sabio",
                            descripcion = "Aumenta tu concentración en los estudios",
                            precio = 80,
                            categoria = CategoriaItem.ACCESORIO.name,
                            imagenRes = R.drawable.item_tienda3,
                            beneficio = "+3% XP extra"
                    ),
                    ItemTienda(
                            id = "item_4",
                            nombre = "Cofre Misterioso",
                            descripcion = "¿Qué secretos contendrá?",
                            precio = 200,
                            categoria = CategoriaItem.ESPECIAL.name,
                            imagenRes = R.drawable.item_tienda4,
                            beneficio = "Recompensa aleatoria"
                    ),
                    ItemTienda(
                            id = "item_5",
                            nombre = "Poción de XP",
                            descripcion = "Duplica tu XP durante 24 horas",
                            precio = 50,
                            categoria = CategoriaItem.BONUS.name,
                            imagenRes = R.drawable.item_tienda5,
                            beneficio = "2x XP por 24 horas"
                    )
            )
}
