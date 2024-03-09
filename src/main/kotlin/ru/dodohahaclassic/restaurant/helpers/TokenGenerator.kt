package ru.dodohahaclassic.restaurant.helpers


object TokenGenerator {
    fun generateToken(tokenLength: Int = 32): String {
        val randomToken = (1..tokenLength)
            .map { (('a'..'z') + ('A'..'Z') + ('0'..'9')).random() }
            .joinToString("")
        return randomToken
    }
}
