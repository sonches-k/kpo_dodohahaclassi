package ru.dodohahaclassic.restaurant.domain.models

import ru.dodohahaclassic.restaurant.interfaces.SerializableInterface

class User : SerializableInterface {
    constructor(login: String, password: String, isAdmin: Boolean, token: String?) {
        this.login = login
        this.password = password
        this.isAdmin = isAdmin
        this.token = token
    }

    private var _id: Int = 0
    private var _login: String = ""
    private var _password: String = ""
    private var _isAdmin: Boolean = false
    private var _token: String? = null

    override var id: Int
        get() {
            return _id
        }
        set(value) {
            _id = value
        }
    var login: String
        get() {
            return _login
        }
        set(value) {
            _login = value
        }
    var password: String
        get() {
            return _password
        }
        set(value) {
            _password = value
        }
    var isAdmin: Boolean
        get() {
            return _isAdmin
        }
        set(value) {
            _isAdmin = value
        }
    var token: String?
        get() {
            return _token
        }
        set(value) {
            _token = value
        }

    fun changeToken(token: String){
        _token = token
    }

    fun clearToken(){
        _token = null
    }

    override fun toString(): String {
        return "User(id=$_id, login=$_login, password=$_password, isAdmin=$_isAdmin, token=$_token)"
    }
}