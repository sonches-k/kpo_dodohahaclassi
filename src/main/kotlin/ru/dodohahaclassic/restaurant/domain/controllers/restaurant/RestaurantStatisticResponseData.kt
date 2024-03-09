package ru.dodohahaclassic.restaurant.domain.controllers.restaurant

import ru.dodohahaclassic.restaurant.domain.models.Order
import ru.dodohahaclassic.restaurant.domain.models.Restaurant

data class RestaurantStatisticResponseData(
    val message: String,
    val restaurant: Restaurant? = null,
    val mostOrderedDishes: Map<Int, Int>? = null,
    val ordersSortedByRating: List<Order>? = null,
    val averageOrderPrice: Double? = null,
)
