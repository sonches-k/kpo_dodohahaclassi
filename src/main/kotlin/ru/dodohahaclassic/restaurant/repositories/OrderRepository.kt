package ru.dodohahaclassic.restaurant.repositories

import org.springframework.util.ResourceUtils
import ru.dodohahaclassic.restaurant.interfaces.RepositoryInterface
import ru.dodohahaclassic.restaurant.domain.models.Order

class OrderRepository : RepositoryInterface<Order> {
    companion object {
        private const val RESOURCE_PATH = "orders.json"
    }

    override val file = ResourceUtils.getFile("classpath:$RESOURCE_PATH");

    override fun create(obj: Order): Boolean {
        val list = readAll().toMutableList()
        obj.id = list.size + 1
        list.add(obj)
        return writeToFile(list)
    }

    override fun read(id: Int): Order? {
        return readAll().firstOrNull { it.id == id }
    }

    override fun readAll(): List<Order> {
        return readFromFile(Order::class.java)
    }

    override fun readByField(fieldName: String, fieldValue: String): Order? {
        val orders = readAll()
        return orders.firstOrNull { it.serialize().contains("\"$fieldName\":\"$fieldValue\"") }
    }

    override fun delete(id: Int): Boolean {
        val list = readAll().toMutableList()
        val objToDelete = list.find { it.id == id }
        objToDelete?.let {
            list.remove(it)
            return writeToFile(list)
        }
        return false
    }

    override fun update(obj: Order): Boolean {
        val list = readAll().toMutableList()
        val existingObj = list.find { it.id == obj.id }
        existingObj?.let {
            list.remove(it)
            list.add(obj)
            return writeToFile(list)
        }
        return false
    }
}
