package ru.dodohahaclassic.restaurant.domain.controllers.order.pay

data class PayOrderRequestData(
    val token: String,
    val orderId: Int
)