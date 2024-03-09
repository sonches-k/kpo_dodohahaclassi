package ru.dodohahaclassic.restaurant.domain.controllers.dish.all

import ru.dodohahaclassic.restaurant.domain.models.Dish

data class GetDishesResponseData(val message: String, val dishes: List<Dish>?)
