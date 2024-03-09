package ru.dodohahaclassic.restaurant.services

import ru.dodohahaclassic.restaurant.exception.AppException
import org.springframework.stereotype.Service
import ru.dodohahaclassic.restaurant.interfaces.RepositoryInterface
import ru.dodohahaclassic.restaurant.domain.models.User
import ru.dodohahaclassic.restaurant.exception.RoleAccessException
import ru.dodohahaclassic.restaurant.exception.ValidationException
import ru.dodohahaclassic.restaurant.helpers.PasswordHasher
import ru.dodohahaclassic.restaurant.helpers.TokenGenerator

@Service
class UserService(private val userRepository: RepositoryInterface<User>) {
    fun register(login: String, password: String, isAdmin: Boolean): String {
        val user = userRepository.readByField("_login", login)
        if (user != null) {
            throw ValidationException("user with this username already exists")
        }

        val hashedPassword = PasswordHasher.hashPassword(password)
        val token = TokenGenerator.generateToken()

        val newUser = User(login, hashedPassword, isAdmin, token)

        val created = userRepository.create(newUser)
        if (!created) {
            throw AppException("user could not be saved to the database")
        }

        return token
    }

    fun authenticate(user: User): String {
        if (user.token != null) {
            throw AppException("the user is already logged in")
        }

        val token = TokenGenerator.generateToken()
        user.changeToken(token)

        val updated = userRepository.update(user)
        if (!updated) {
            throw AppException("failed to update the user")
        }

        return token
    }

    fun logout(user: User): Boolean {
        if (user.token == null) {
            throw AppException("the user is not logged in")
        }

        user.clearToken()

        val updated = userRepository.update(user)
        if (!updated) {
            throw AppException("failed to update the user")
        }

        return true
    }

    fun getUsers(user: User): List<User> {
        if (!user.isAdmin) {
            throw RoleAccessException()
        }

        val users = userRepository.readAll()

        return users
    }

    fun getUser(login: String, password: String): User {
        val user = userRepository.readByField("_login", login)
                ?: throw AppException("user with this username does not exist")

        val verified = PasswordHasher.verifyPassword(password, user.password)
        if (!verified) {
            throw AppException("invalid password")
        }

        return user
    }

    fun getUser(token: String): User {
        val user = userRepository.readByField("_token", token)
                ?: throw AppException("user with such a token does not exist")

        return user
    }
}