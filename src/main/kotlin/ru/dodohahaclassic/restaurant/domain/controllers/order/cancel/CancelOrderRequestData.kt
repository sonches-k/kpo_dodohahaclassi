package ru.dodohahaclassic.restaurant.domain.controllers.order.cancel

data class CancelOrderRequestData(
    val token: String,
    val orderId: Int,
)