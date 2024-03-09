package ru.dodohahaclassic.restaurant.domain.controllers.restaurant

import ru.dodohahaclassic.restaurant.domain.models.Restaurant

data class RestaurantInfoResponseData(val message: String, val restaurant: Restaurant? = null)
