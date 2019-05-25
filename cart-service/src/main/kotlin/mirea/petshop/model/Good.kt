package mirea.petshop.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

enum class GoodType { Pet, Item, Unknown }

object Goods : IntIdTable("Goods") {
    val nameColumn = text("name")
    val descriptionColumn = text("description")
    val priceColumn = decimal("price", 10, 2)
    val amountColumn = integer("amount")
    //val typeColumn = customEnumeration("type", "GoodType", { value -> GoodType.valueOf(value as String) }, { PGEnum("goodtype", it) })
}


@JsonIgnoreProperties("writeValues", "readValues", "_readValues", "db", "klass")
class GoodWrapper(id: EntityID<Int>) : IntEntity(id) {
    companion object Table : IntEntityClass<GoodWrapper>(Goods)

    val ID: Int by lazy {
        id.value
    }

    var name by Goods.nameColumn
    var price by Goods.priceColumn
}

/*data class Good(val ID: Int, var name: String, var description: String, var price: BigDecimal, var amount: Int, var type: GoodType)

class GoodWrapper (g: Good) {
    val ID = g.ID

    var name = g.name
    var price = g.price
}*/