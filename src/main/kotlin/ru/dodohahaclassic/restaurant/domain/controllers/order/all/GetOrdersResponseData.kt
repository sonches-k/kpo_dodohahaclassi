package ru.dodohahaclassic.restaurant.domain.controllers.order.all

import ru.dodohahaclassic.restaurant.domain.models.Order

data class GetOrdersResponseData(val message: String, val orders: List<Order>?)
