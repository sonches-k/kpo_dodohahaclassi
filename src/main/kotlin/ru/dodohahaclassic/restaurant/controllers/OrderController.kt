package ru.dodohahaclassic.restaurant.controllers


import ru.dodohahaclassic.restaurant.exception.AppException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.dodohahaclassic.restaurant.domain.controllers.order.addDishes.AddDishesToOrderRequestData
import ru.dodohahaclassic.restaurant.domain.controllers.order.addDishes.AddDishesToOrderResponseData
import ru.dodohahaclassic.restaurant.domain.controllers.order.all.GetOrdersResponseData
import ru.dodohahaclassic.restaurant.domain.controllers.order.cancel.CancelOrderRequestData
import ru.dodohahaclassic.restaurant.domain.controllers.order.cancel.CancelOrderResponseData
import ru.dodohahaclassic.restaurant.domain.controllers.order.pay.PayOrderRequestData
import ru.dodohahaclassic.restaurant.domain.controllers.order.pay.PayOrderResponseData
import ru.dodohahaclassic.restaurant.domain.controllers.order.rate.RateOrderResponseData
import ru.dodohahaclassic.restaurant.domain.controllers.order.place.PlaceOrderRequestData
import ru.dodohahaclassic.restaurant.domain.controllers.order.place.PlaceOrderResponseData
import ru.dodohahaclassic.restaurant.domain.controllers.order.rate.RateOrderRequestData
import ru.dodohahaclassic.restaurant.domain.controllers.order.removeDishes.RemoveDishFromOrderRequestData
import ru.dodohahaclassic.restaurant.domain.controllers.order.removeDishes.RemoveDishFromOrderResponseData
import ru.dodohahaclassic.restaurant.services.OrderService
import ru.dodohahaclassic.restaurant.services.UserService

@RestController
@RequestMapping("/order")
class OrderController(
    private val userService: UserService,
    private val orderService: OrderService,
) {
    @PostMapping("/place")
    fun placeOrder(
        @RequestBody requestData: PlaceOrderRequestData,
    ): ResponseEntity<PlaceOrderResponseData> {
        return try {
            val user = userService.getUser(requestData.token)
            val orderId = orderService.placeOrder(
                user, requestData.orderList
            )
            ResponseEntity.ok(PlaceOrderResponseData("The order has been added", orderId))
        } catch (e: AppException) {
            ResponseEntity.badRequest().body(PlaceOrderResponseData(e.message.toString()))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(PlaceOrderResponseData("bad request"))
        }
    }

    @PostMapping("/admin/addToOrder")
    fun addDishesToOrder(
        @RequestBody requestData: AddDishesToOrderRequestData,
    ): ResponseEntity<AddDishesToOrderResponseData> {
        return try {
            val user = userService.getUser(requestData.token)
            orderService.addToOrder(
                user, requestData.orderId, requestData.orderList
            )
            ResponseEntity.ok(AddDishesToOrderResponseData("The dishes were successfully added to the order"))
        } catch (e: AppException) {
            ResponseEntity.badRequest().body(AddDishesToOrderResponseData(e.message.toString()))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(AddDishesToOrderResponseData("bad request"))
        }
    }

    @DeleteMapping("/admin/removeFromOrder")
    fun removeDishesToOrder(
        @RequestBody requestData: RemoveDishFromOrderRequestData,
    ): ResponseEntity<RemoveDishFromOrderResponseData> {
        return try {
            val user = userService.getUser(requestData.token)
            orderService.removeFromOrder(
                user, requestData.orderId, requestData.orderList
            )
            ResponseEntity.ok(RemoveDishFromOrderResponseData("The dishes have been removed from the order"))
        } catch (e: AppException) {
            ResponseEntity.badRequest().body(RemoveDishFromOrderResponseData(e.message.toString()))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(RemoveDishFromOrderResponseData("bad request"))
        }
    }

    @PostMapping("/admin/cancel")
    fun cancelOrder(
        @RequestBody requestData: CancelOrderRequestData,
    ): ResponseEntity<CancelOrderResponseData> {
        return try {
            val user = userService.getUser(requestData.token)
            orderService.cancelOrder(
                user, requestData.orderId
            )
            ResponseEntity.ok(CancelOrderResponseData("The order has been cancelled"))
        } catch (e: AppException) {
            ResponseEntity.badRequest().body(CancelOrderResponseData(e.message.toString()))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(CancelOrderResponseData("bad request"))
        }
    }

    @PostMapping("/admin/pay")
    fun payOrder(
        @RequestBody requestData: PayOrderRequestData,
    ): ResponseEntity<PayOrderResponseData> {
        return try {
            val user = userService.getUser(requestData.token)
            orderService.payOrder(
                user, requestData.orderId
            )
            ResponseEntity.ok(PayOrderResponseData("The order has been paid for"))
        } catch (e: AppException) {
            ResponseEntity.badRequest().body(PayOrderResponseData(e.message.toString()))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(PayOrderResponseData("bad request"))
        }
    }

    @GetMapping("/all")
    fun getAllDishes(
        @RequestParam token: String,
    ): ResponseEntity<GetOrdersResponseData> {
        return try {
            val user = userService.getUser(token)
            val orders = orderService.getOrders(user)
            ResponseEntity.ok(GetOrdersResponseData(
                "Information about all orders has been successfully received",
                orders))
        } catch (e: AppException) {
            ResponseEntity.badRequest().body(GetOrdersResponseData(e.message.toString(), null))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(GetOrdersResponseData("bad request", null))
        }
    }

    @PostMapping("/rate")
    fun rateOrder(
        @RequestBody requestData: RateOrderRequestData,
    ): ResponseEntity<RateOrderResponseData> {
        return try {
            val user = userService.getUser(requestData.token)
            orderService.rateOrder(
                user, requestData.orderId, requestData.mark, requestData.comment
            )
            ResponseEntity.ok(RateOrderResponseData("The order was rated. Thank you!"))
        } catch (e: AppException) {
            ResponseEntity.badRequest().body(RateOrderResponseData(e.message.toString()))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(RateOrderResponseData("bad request"))
        }
    }
}
