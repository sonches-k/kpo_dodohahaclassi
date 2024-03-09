package ru.dodohahaclassic.restaurant.domain.controllers.dish.delete

data class DeleteDishRequestData(
    val token: String,
    val dishId: Int
)
