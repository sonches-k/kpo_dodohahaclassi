package ru.dodohahaclassic.restaurant.services

import jakarta.annotation.PreDestroy
import kotlinx.coroutines.delay
import ru.dodohahaclassic.restaurant.exception.AppException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.dodohahaclassic.restaurant.domain.enums.OrderStatus
import ru.dodohahaclassic.restaurant.interfaces.RepositoryInterface
import ru.dodohahaclassic.restaurant.domain.models.Dish
import ru.dodohahaclassic.restaurant.domain.models.Order
import ru.dodohahaclassic.restaurant.domain.models.Restaurant
import ru.dodohahaclassic.restaurant.domain.models.User
import ru.dodohahaclassic.restaurant.repositories.UserRepository
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Service
class OrderBackgroundService(
        private val orderRepository: RepositoryInterface<Order>,
        private val dishRepository: RepositoryInterface<Dish>,
        private val userRepository: RepositoryInterface<User>,
        @Value("2") final val restaurantWorkers: Int,
) {
    private val logger: Logger = LoggerFactory.getLogger(OrderBackgroundService::class.java)
    private val executor: ExecutorService = Executors.newFixedThreadPool(restaurantWorkers)

    fun processOrder(orderId: Int): Int {
        executor.submit {
            try {
                /*
                Поток ожидает, пока блюдо не начнет готовиться, включая время,
                необходимое на запись заказа, перемещение официантки, подготовку поваров и т.д.
                По прошествии этого времени, начиная с момента, когда заказ начнут готовить,
                отменить заказ или внести изменения в количество блюд и прочее будет невозможно.
                 */
                Thread.sleep(60 * 2 * 1000)
                val order = orderRepository.read(orderId) ?: throw AppException("Order with id $orderId not found")

                if (order.status != OrderStatus.ACCEPTED) {
                    throw AppException("Order with id $orderId is already being processed or completed")
                }

                order.changeStatus(OrderStatus.PREPARING)
                orderRepository.update(order)

                val allTimeSeconds = order.dishes.sumOf { orderPosition ->
                    val dish = dishRepository.read(orderPosition.dishId)
                            ?: throw AppException("Item with id ${orderPosition.dishId} not found in the menu")
                    orderPosition.quantity * dish.cookingTimeMinutes * 60
                }.toLong()

                logger.info("Processing order: $order. It will take ${allTimeSeconds / 60} minutes")

                Thread.sleep(allTimeSeconds * 1000)

                order.changeStatus(OrderStatus.READY)
                orderRepository.update(order)

                val restaurant = Restaurant.getInstance()
                restaurant.updateRevenue(order.price)

                logger.info("Order processed: $order")
            } catch (e: Exception) {
                logger.error("Error processing order: $orderId - ${e.message}")
            }
        }

        return orderId
    }

    fun cancel(orderId: Int) {
        executor.submit {
            val order = orderRepository.read(orderId)
                    ?: throw AppException("Order with id $orderId not found")

            if (order.status != OrderStatus.PREPARING) {
                throw AppException("Order with id $orderId is not currently being prepared")
            }

            order.changeStatus(OrderStatus.CANCELED)
            orderRepository.update(order)
        }
    }

    fun updateWorkers(user: User): Boolean {
        if (!user.isAdmin) {
            throw AppException("User ${user.login} is not an administrator")
        }

        val restaurantWorkers = userRepository.readAll().count { it.isAdmin }
        val restaurant = Restaurant.getInstance()
        restaurant.workers = restaurantWorkers
        return true
    }

    @PreDestroy
    fun cleanUp() {
        executor.shutdown()
    }
}