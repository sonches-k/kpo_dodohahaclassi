package ru.dodohahaclassic.restaurant.services

import ru.dodohahaclassic.restaurant.exception.AppException
import org.springframework.stereotype.Service
import ru.dodohahaclassic.restaurant.domain.enums.OrderStatus
import ru.dodohahaclassic.restaurant.interfaces.RepositoryInterface
import ru.dodohahaclassic.restaurant.domain.models.*
import ru.dodohahaclassic.restaurant.exception.RoleAccessException

@Service
class OrderService(
        private val orderRepository: RepositoryInterface<Order>,
        private val dishRepository: RepositoryInterface<Dish>,
        private val orderBackgroundService: OrderBackgroundService) {
    fun placeOrder(user: User, orderList: List<OrderPosition>): Int {
        val newOrder = Order(user.id)

        for (orderPosition in orderList) {
            val dish = dishRepository.read(orderPosition.dishId)
                    ?: throw AppException("There is no item in the menu with id ${orderPosition.dishId}")

            if (orderPosition.quantity > dish.quantity) {
                throw AppException("You cannot order ${orderPosition.quantity} " +
                        "portions, as there are only ${dish.quantity} available in the menu")
            }

            dish.subQuantity(orderPosition.quantity)
            dishRepository.update(dish)

            newOrder.addDish(orderPosition)
            newOrder.addPrice(dish.price * orderPosition.quantity)
        }

        val created = orderRepository.create(newOrder)
        if (!created) {
            throw AppException("Order could not be saved to the database")
        }

        val id = orderBackgroundService.processOrder(newOrder.id)

        return id
    }

    fun addToOrder(user: User, orderId: Int, orderList: List<OrderPosition>): Boolean {
        if (!user.isAdmin) {
            throw RoleAccessException()
        }

        val order = orderRepository.read(orderId) ?: throw AppException("Order with id $orderId not found")

        if (order.status == OrderStatus.PREPARING) {
            throw AppException("The order is currently being prepared")
        }

        if (order.status == OrderStatus.READY) {
            throw AppException("The order is already prepared")
        }

        if (order.userId != user.id) {
            throw AppException("Order with id $orderId does not belong to user ${user.login}")
        }

        for (orderPosition in orderList) {
            val dish = dishRepository.read(orderPosition.dishId)
                    ?: throw AppException("There is no item in the menu with id ${orderPosition.dishId}")

            dish.subQuantity(orderPosition.quantity)
            dishRepository.update(dish)

            order.addDish(orderPosition)
            order.addPrice(dish.price * orderPosition.quantity)
        }

        val updated = orderRepository.update(order)
        if (!updated) {
            throw AppException("failed to update the order")
        }

        return true
    }

    fun removeFromOrder(user: User, orderId: Int, orderList: List<OrderPosition>): Boolean {
        if (!user.isAdmin) {
            throw RoleAccessException()
        }

        val order = orderRepository.read(orderId) ?: throw AppException("Order with id $orderId not found")

        if (order.userId != user.id) {
            throw AppException("Order with id $orderId does not belong to user ${user.login}")
        }

        if (order.status == OrderStatus.PREPARING) {
            throw AppException("The order is currently being prepared")
        }

        if (order.status == OrderStatus.READY) {
            throw AppException("The order is already prepared")
        }

        for (orderPosition in orderList) {
            val actualOrderPosition = order.dishes.firstOrNull { it.dishId == orderPosition.dishId }
            if (actualOrderPosition == null) {
                throw AppException("There is no item with id ${orderPosition.dishId} in the order")
            }

            val dish = dishRepository.read(orderPosition.dishId)
                    ?: throw AppException("There is no item in the menu with id ${orderPosition.dishId}")

            dish.addQuantity(orderPosition.quantity)
            dishRepository.update(dish)

            order.removeDish(orderPosition)
            order.subPrice(dish.price * orderPosition.quantity)
        }
        val updated = orderRepository.update(order)
        if (!updated) {
            throw AppException("failed to update the order")
        }

        return true
    }

    fun cancelOrder(user: User, orderId: Int): Boolean {
        if (!user.isAdmin) {
            throw RoleAccessException()
        }

        val order = orderRepository.read(orderId) ?: throw AppException("Order with id $orderId not found")
        if (order.userId != user.id) {
            throw AppException("Order with id $orderId does not belong to user ${user.login}")
        }

        if (order.status == OrderStatus.READY) {
            throw AppException("The order is already prepared")
        }

        orderBackgroundService.cancel(orderId)
        return true
    }

    fun payOrder(user: User, orderId: Int): Boolean {
        if (!user.isAdmin) {
            throw RoleAccessException()
        }

        val order = orderRepository.read(orderId) ?: throw AppException("Order with id $orderId not found")

        if (order.userId != user.id) {
            throw AppException("Order with id $orderId does not belong to user ${user.login}")
        }

        if (order.status == OrderStatus.PAID) {
            throw AppException("The order is already paid")
        }

        if (order.status == OrderStatus.CANCELED) {
            throw AppException("The order has been canceled")
        }

        if (order.status != OrderStatus.READY) {
            throw AppException("The order is not ready yet")
        }

        order.changeStatus(OrderStatus.PAID)

        val updated = orderRepository.update(order)

        if (!updated) {
            throw AppException("Database connection error")
        }

        return true
    }

    fun rateOrder(user: User, orderId: Int, mark: Int, comment: String): Boolean {
        if (!user.isAdmin) {
            throw RoleAccessException()
        }

        val order = orderRepository.read(orderId) ?: throw AppException("Order with id $orderId not found")

        if (order.userId != user.id) {
            throw AppException("Order with id $orderId does not belong to user ${user.login}")
        }

        if (order.status != OrderStatus.PAID) {
            throw AppException("The order is not paid")
        }

        val review = Review(mark, comment)
        order.changeReview(review)
        val updated = orderRepository.update(order)
        if (!updated) {
            throw AppException("Database connection error")
        }
        return true
    }

    fun getOrders(user: User): List<Order> {
        return if (user.isAdmin) {
            orderRepository.readAll()
        } else {
            orderRepository.readAll().filter { it.userId == user.id }
        }
    }

    fun updateTotalRevenue(user: User): Boolean {
        if (!user.isAdmin) {
            throw RoleAccessException()
        }

        val totalRevenue = orderRepository.readAll().filter { it.status == OrderStatus.PAID }.sumOf { it.price }
        val restaurant = Restaurant.getInstance()
        restaurant.totalRevenue = totalRevenue
        return true
    }

    fun mostOrderedDishes(): Map<Int, Int> {
        val allOrders = orderRepository.readAll()
        val dishCountMap = mutableMapOf<Int, Int>()
        allOrders.forEach { order ->
            order.dishes.forEach { orderPosition ->
                val dishId = orderPosition.dishId
                dishCountMap[dishId] = dishCountMap.getOrDefault(dishId, 0) + orderPosition.quantity
            }
        }
        return dishCountMap.toList().sortedByDescending { (_, quantity) -> quantity }.toMap()
    }


    fun ordersSortedByRating(): List<Order> {
        return orderRepository.readAll().filter { it.review != null }.sortedByDescending { it.review?.mark }
    }

    fun averageOrderPrice(): Double {
        val allOrders = orderRepository.readAll()
        val totalAmount = allOrders.sumOf { it.price }
        return if (allOrders.isNotEmpty()) totalAmount / allOrders.size else 0.0
    }
}
