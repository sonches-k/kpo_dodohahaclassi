package ru.dodohahaclassic.restaurant.domain.controllers.order.removeDishes

import ru.dodohahaclassic.restaurant.domain.models.OrderPosition

data class RemoveDishFromOrderRequestData(
    val token: String,
    val orderId: Int,
    val orderList: List<OrderPosition>
)