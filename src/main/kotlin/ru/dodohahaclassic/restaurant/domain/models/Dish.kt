package ru.dodohahaclassic.restaurant.domain.models

import ru.dodohahaclassic.restaurant.interfaces.SerializableInterface
import ru.dodohahaclassic.restaurant.exception.ValidationException

class Dish : SerializableInterface {
    constructor(name: String, description: String, quantity: Int, cookingTimeMinutes: Int, price: Double) {
        if (quantity <= 0) {
            throw ValidationException("Quantity must be positive")
        }

        if (cookingTimeMinutes <= 0) {
            throw ValidationException("Cooking time in minutes must be positive")
        }

        if (price < 0) {
            throw ValidationException("Price must be non-negative")
        }

        this.name = name
        this.description = description
        this.quantity = quantity
        this.cookingTimeMinutes = cookingTimeMinutes
        this.price = price
    }

    private var _id: Int = 0
    private var _name: String = ""
    private var _description: String = ""
    private var _quantity: Int = 0
    private var _cookingTime: Int = 0
    private var _price: Double = 0.0

    override var id: Int
        get() {
            return _id
        }
        set(value) {
            _id = value
        }
    var name: String
        get() {
            return _name
        }
        set(value) {
            _name = value
        }
    var description: String
        get() {
            return _description
        }
        set(value) {
            _description = value
        }
    var quantity: Int
        get() {
            return _quantity
        }
        set(value) {
            _quantity = value
        }
    var cookingTimeMinutes: Int
        get() {
            return _cookingTime
        }
        set(value) {
            _cookingTime = value
        }
    var price: Double
        get() {
            return _price
        }
        set(value) {
            _price = value
        }

    fun subQuantity(quantity: Int) {
        _quantity -= quantity
    }

    fun addQuantity(quantity: Int){
        _quantity += quantity
    }

    override fun toString(): String {
        return "Dish(id=$_id, name=$_name, description=$_description, quantity=$_quantity, cookingTime=$_cookingTime, price=$_price)"
    }
}
