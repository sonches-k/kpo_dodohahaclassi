package ru.dodohahaclassic.restaurant.controllers

import ru.dodohahaclassic.restaurant.exception.AppException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.dodohahaclassic.restaurant.domain.controllers.dish.add.AddDishRequestData
import ru.dodohahaclassic.restaurant.domain.controllers.dish.add.AddDishResponseData
import ru.dodohahaclassic.restaurant.domain.controllers.dish.all.GetDishesResponseData
import ru.dodohahaclassic.restaurant.domain.controllers.dish.delete.DeleteDishRequestData
import ru.dodohahaclassic.restaurant.domain.controllers.dish.delete.DeleteDishResponseData
import ru.dodohahaclassic.restaurant.services.DishService
import ru.dodohahaclassic.restaurant.services.UserService

@RestController
@RequestMapping("/dish")
class DishController(
        private val userService: UserService,
        private val dishService: DishService) {
    @PostMapping("/admin/create")
    fun addDish(
            @RequestBody requestData: AddDishRequestData,
    ): ResponseEntity<AddDishResponseData> {
        return try {
            val user = userService.getUser(requestData.token)
            dishService.addDish(
                    user,
                    requestData.dishName,
                    requestData.dishDescription,
                    requestData.dishQuantity,
                    requestData.dishCookingTimeMinutes,
                    requestData.dishPrice
            )
            ResponseEntity.ok(AddDishResponseData("dish was successfully created"))
        } catch (e: AppException) {
            ResponseEntity.badRequest().body(AddDishResponseData(e.message.toString()))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(AddDishResponseData("bad request"))
        }
    }

    @DeleteMapping("/admin/delete")
    fun deleteDish(
            @RequestBody requestData: DeleteDishRequestData,
    ): ResponseEntity<DeleteDishResponseData> {
        return try {
            val user = userService.getUser(requestData.token)
            dishService.deleteDish(user, requestData.dishId)
            ResponseEntity.ok(DeleteDishResponseData("dish was successfully deleted"))
        } catch (e: AppException) {
            ResponseEntity.badRequest().body(DeleteDishResponseData(e.message.toString()))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(DeleteDishResponseData("bad request"))
        }
    }

    @GetMapping("/all")
    fun getAllDishes(
            @RequestParam token: String,
    ): ResponseEntity<GetDishesResponseData> {
        return try {
            val user = userService.getUser(token)
            val dishes = dishService.getDishes(user)
            ResponseEntity.ok(GetDishesResponseData("dishes list was successfully returned", dishes))
        } catch (e: AppException) {
            ResponseEntity.badRequest().body(GetDishesResponseData(e.message.toString(), null))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(GetDishesResponseData("bad request", null))
        }
    }
}
