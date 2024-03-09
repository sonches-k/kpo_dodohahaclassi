package ru.dodohahaclassic.restaurant.controllers

import ru.dodohahaclassic.restaurant.exception.AppException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.dodohahaclassic.restaurant.domain.controllers.user.all.UsersResponseData
import ru.dodohahaclassic.restaurant.domain.controllers.user.auth.AuthRequestData
import ru.dodohahaclassic.restaurant.domain.controllers.user.auth.AuthResponseData
import ru.dodohahaclassic.restaurant.domain.controllers.user.logout.LogoutRequestData
import ru.dodohahaclassic.restaurant.domain.controllers.user.logout.LogoutResponseData
import ru.dodohahaclassic.restaurant.domain.controllers.user.register.RegisterRequestData
import ru.dodohahaclassic.restaurant.domain.controllers.user.register.RegisterResponseData
import ru.dodohahaclassic.restaurant.services.UserService


@RestController
class UserController(private val userService: UserService) {
    @PostMapping("/identity/register")
    fun register(
            @RequestBody registerRequestData: RegisterRequestData,
    ): ResponseEntity<RegisterResponseData> {
        return try {
            val token = userService.register(registerRequestData.login, registerRequestData.password, registerRequestData.isAdmin)
            ResponseEntity.ok(RegisterResponseData("user is registered", token))
        } catch (e: AppException) {
            ResponseEntity.badRequest().body(RegisterResponseData(e.message.toString()))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(RegisterResponseData("bad request"))
        }
    }

    @PostMapping("/identity/auth")
    fun auth(
            @RequestBody authRequestData: AuthRequestData,
    ): ResponseEntity<AuthResponseData> {
        return try {
            val user = userService.getUser(authRequestData.login, authRequestData.password)
            val token = userService.authenticate(user)
            ResponseEntity.ok(AuthResponseData("user is logged in system", token))
        } catch (e: AppException) {
            ResponseEntity.badRequest().body(AuthResponseData(e.message.toString()))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(AuthResponseData("bad request"))
        }
    }

    @PostMapping("/identity/logout")
    fun logout(
            @RequestBody logoutRequestData: LogoutRequestData,
    ): ResponseEntity<LogoutResponseData> {
        return try {
            val user = userService.getUser(logoutRequestData.token)
            userService.logout(user)
            ResponseEntity.ok(LogoutResponseData("user logged out"))
        } catch (e: AppException) {
            ResponseEntity.badRequest().body(LogoutResponseData(e.message.toString()))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(LogoutResponseData("bad request"))
        }
    }

    @GetMapping("/admin/users")
    fun users(@RequestParam token: String): ResponseEntity<UsersResponseData> {
        println(token)
        return try {
            val user = userService.getUser(token)
            val users = userService.getUsers(user)
            ResponseEntity.ok(UsersResponseData(
                "information about all users was successfully received",
                users))
        } catch (e: AppException) {
            ResponseEntity.badRequest().body(UsersResponseData(e.message.toString(), null))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(UsersResponseData("bad request", null))
        }
    }
}
