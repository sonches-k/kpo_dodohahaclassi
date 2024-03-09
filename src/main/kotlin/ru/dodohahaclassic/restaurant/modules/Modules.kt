package ru.dodohahaclassic.restaurant.modules

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.dodohahaclassic.restaurant.interfaces.RepositoryInterface
import ru.dodohahaclassic.restaurant.domain.models.Dish
import ru.dodohahaclassic.restaurant.domain.models.Order
import ru.dodohahaclassic.restaurant.domain.models.User
import ru.dodohahaclassic.restaurant.repositories.DishRepository
import ru.dodohahaclassic.restaurant.repositories.OrderRepository
import ru.dodohahaclassic.restaurant.repositories.UserRepository


@Configuration
class Modules {
    @Bean
    fun userRepository(): RepositoryInterface<User> {
        return UserRepository()
    }

    @Bean
    fun dishRepository(): RepositoryInterface<Dish> {
        return DishRepository()
    }

    @Bean
    fun orderRepository(): RepositoryInterface<Order> {
        return OrderRepository()
    }

}