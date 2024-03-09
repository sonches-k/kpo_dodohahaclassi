package ru.dodohahaclassic.restaurant.controllers

import ru.dodohahaclassic.restaurant.exception.AppException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.dodohahaclassic.restaurant.domain.controllers.restaurant.RestaurantInfoResponseData
import ru.dodohahaclassic.restaurant.domain.controllers.restaurant.RestaurantStatisticResponseData
import ru.dodohahaclassic.restaurant.domain.models.Restaurant
import ru.dodohahaclassic.restaurant.services.OrderBackgroundService
import ru.dodohahaclassic.restaurant.services.OrderService
import ru.dodohahaclassic.restaurant.services.UserService

@RestController
@RequestMapping("/restaurant")
class RestaurantController(
        private val userService: UserService,
        private val orderService: OrderService,
        private val orderBackgroundService: OrderBackgroundService,
) {
    @GetMapping("/admin/info")
    fun restaurantStatus(@RequestParam token: String): ResponseEntity<RestaurantInfoResponseData> {
        try {
            val user = userService.getUser(token)
            orderService.updateTotalRevenue(user)
            orderBackgroundService.updateWorkers(user)
            return ResponseEntity.ok(RestaurantInfoResponseData("The data about restaurant", Restaurant.getInstance()))
        } catch (e: AppException) {
            return ResponseEntity.badRequest().body(RestaurantInfoResponseData(e.message.toString()))
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(RestaurantInfoResponseData("bad request"))
        }
    }

    @GetMapping("/admin/statistics")
    fun restaurantStatistic(@RequestParam token: String): ResponseEntity<RestaurantStatisticResponseData> {
        try {
            val user = userService.getUser(token)
            orderService.updateTotalRevenue(user)
            orderBackgroundService.updateWorkers(user)
            val mostOrderedDishes = orderService.mostOrderedDishes()
            val ordersSortedByRating = orderService.ordersSortedByRating()
            val averageOrderPrice = orderService.averageOrderPrice()
            return ResponseEntity.ok(
                RestaurantStatisticResponseData(
                    "Statistics",
                    Restaurant.getInstance(),
                    mostOrderedDishes,
                    ordersSortedByRating,
                    averageOrderPrice
                )
            )
        } catch (e: AppException) {
            return ResponseEntity.badRequest().body(RestaurantStatisticResponseData(e.message.toString()))
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(RestaurantStatisticResponseData("bad request"))
        }
    }
}
