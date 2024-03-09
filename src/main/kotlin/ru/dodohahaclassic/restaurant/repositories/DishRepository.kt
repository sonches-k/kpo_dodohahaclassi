package ru.dodohahaclassic.restaurant.repositories

import org.springframework.util.ResourceUtils
import ru.dodohahaclassic.restaurant.interfaces.RepositoryInterface
import ru.dodohahaclassic.restaurant.domain.models.Dish

class DishRepository : RepositoryInterface<Dish> {
    companion object {
        private const val RESOURCE_PATH = "dishes.json"
    }

    override val file = ResourceUtils.getFile("classpath:$RESOURCE_PATH");

    override fun create(obj: Dish): Boolean {
        val list = readAll().toMutableList()
        obj.id = list.size + 1
        list.add(obj)
        return writeToFile(list)
    }

    override fun read(id: Int): Dish? {
        return readAll().firstOrNull { it.id == id }
    }

    override fun readAll(): List<Dish> {
        return readFromFile(Dish::class.java)
    }

    override fun readByField(fieldName: String, fieldValue: String): Dish? {
        val dishes = readAll()
        return dishes.firstOrNull { it.serialize().contains("\"$fieldName\":\"$fieldValue\"") }
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

    override fun update(obj: Dish): Boolean {
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
