package ru.dodohahaclassic.restaurant.domain.controllers.order.rate

data class RateOrderRequestData(
    val token: String,
    val orderId: Int,
    var mark: Int,
    var comment: String
)