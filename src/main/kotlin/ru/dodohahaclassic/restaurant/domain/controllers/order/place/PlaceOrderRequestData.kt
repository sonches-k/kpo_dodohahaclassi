package ru.dodohahaclassic.restaurant.domain.controllers.order.place

import ru.dodohahaclassic.restaurant.domain.models.OrderPosition

class PlaceOrderRequestData(
    val token: String,
    val orderList: List<OrderPosition>,
)
