package ru.dodohahaclassic.restaurant.services

import ru.dodohahaclassic.restaurant.exception.AppException
import org.springframework.stereotype.Service
import ru.dodohahaclassic.restaurant.domain.enums.OrderStatus
import ru.dodohahaclassic.restaurant.interfaces.RepositoryInterface
import ru.dodohahaclassic.restaurant.domain.models.Dish
import ru.dodohahaclassic.restaurant.domain.models.User
import ru.dodohahaclassic.restaurant.exception.RoleAccessException

@Service
class DishService(
        private val dishRepository: RepositoryInterface<Dish>,
        private val orderService: OrderService) {
    fun addDish(
            user: User,
            name: String,
            description: String,
            quantity: Int,
            dishCookingTimeMinutes: Int,
            price: Double,
    ): Boolean {
        if (!user.isAdmin) {
            throw RoleAccessException()
        }

        val newDish = Dish(name, description, quantity, dishCookingTimeMinutes, price)
        val created = dishRepository.create(newDish)

        if (!created) {
            throw AppException("Dish could not be saved to the database")
        }

        return true
    }

    fun deleteDish(user: User, dishId: Int): Boolean {
        if (!user.isAdmin) {
            throw RoleAccessException()
        }

        val orders = orderService.getOrders(user)
        for (order in orders) {
            if (order.status == OrderStatus.READY) {
                continue
            }

            for (dish in order.dishes) {
                if (dish.dishId == dishId) {
                    throw AppException("You cannot remove the dish as it is in the process of cooking")
                }
            }
        }

        val deleted = dishRepository.delete(dishId)
        if (!deleted) {
            throw AppException("The dish was not found")
        }
        return true
    }

    fun getDishes(user: User): List<Dish> {
        return dishRepository.readAll()
    }

}
