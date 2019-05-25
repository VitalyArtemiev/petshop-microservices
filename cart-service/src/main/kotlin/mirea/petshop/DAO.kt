package mirea.petshop

import org.springframework.stereotype.Component

@Component
interface DAO<T, TW> {
    fun get(id: Int): T?

    fun getAll(): List<TW>
}