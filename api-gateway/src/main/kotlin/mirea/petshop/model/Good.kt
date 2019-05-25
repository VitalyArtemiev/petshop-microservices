package mirea.petshop.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import mirea.petshop.DAO
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.postgresql.util.PGobject
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

/*open class Good(id: Int, nameColumn: String, amt: Int, priceColumn: Double) {
    val id = id
    val Name = nameColumn
    var Amount = amt
    val Price = priceColumn
}

class GoodWrapper(pet: Good) {
    val id = pet.id
    val nameColumn = pet.nameColumn
    val priceColumn = pet.priceColumn
}*/

enum class GoodType { Pet, Item, Unknown }

class Good(
        val id: Int,

        var name: String,
        var description: String,
        var price: BigDecimal,
        var amount: Int,
        var type: GoodType
)

class GoodWrapper(val id: Int, var name: String, var price: BigDecimal) {
    constructor(g: Good): this(g.id, g.name, g.price)
}