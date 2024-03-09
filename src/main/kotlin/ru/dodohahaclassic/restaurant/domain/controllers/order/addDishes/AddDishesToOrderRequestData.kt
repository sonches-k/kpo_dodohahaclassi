package ru.dodohahaclassic.restaurant.domain.controllers.order.addDishes

import ru.dodohahaclassic.restaurant.domain.models.OrderPosition

data class AddDishesToOrderRequestData(
    val token: String,
    val orderId: Int,
    val orderList: List<OrderPosition>,
)