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
    val ID = id
    val Name = nameColumn
    var Amount = amt
    val Price = priceColumn
}

class GoodWrapper(pet: Good) {
    val ID = pet.id
    val nameColumn = pet.nameColumn
    val priceColumn = pet.priceColumn
}*/

enum class GoodType { Pet, Item, Unknown }

class PGEnum<T : Enum<T>>(enumTypeName: String, enumValue: T?) : PGobject() {  //user enum for postgres
    init {
        value = enumValue?.name
        type = enumTypeName
    }
}

@Repository
@Transactional
class GoodDAO : DAO<Good, GoodWrapper> {


    override fun get(id: Int): Good? {
        return Good.findById(id)
    }

    override fun getAll(): List<GoodWrapper> {
        return GoodWrapper.all().toList()
    }

    fun save(name: String, description: String, price: BigDecimal, amount: Int, type: GoodType) {
        Good.new {
            this.name = name
            this.description = description
            this.price = price
            this.amount = amount
            this.type = type
        }
    }

    fun update(id: Int = -1, name: String = "", description: String = "", price: BigDecimal = -BigDecimal.ONE,
               amount: Int = -1, type: GoodType = GoodType.Unknown): Boolean {
        val g: Good? = if (id >= 0)
            Good.findById(id)
        else
            Good.find { Goods.nameColumn eq name }.first()

        return if (g == null)
            false
        else {
            when {
                name != "" -> g.name = name
                description != "" -> g.description = description
                price != -BigDecimal.ONE -> g.price = price
                amount >= 0 -> g.amount = amount
                type != GoodType.Unknown -> g.type = type
            }

            true
        }
    }

}

object Goods : IntIdTable("Goods") {
    //val id = integer("id").autoIncrement().primaryKey()
    val nameColumn = text("name")
    val descriptionColumn = text("description")
    val priceColumn = decimal("price", 10, 2)
    val amountColumn = integer("amount")
    val typeColumn = customEnumeration("type", "GoodType", { value -> GoodType.valueOf(value as String) }, { PGEnum("goodtype", it) })
}

@JsonIgnoreProperties("writeValues", "readValues", "_readValues", "db", "klass")
class Good(id: EntityID<Int>) : IntEntity(id) {
    companion object Table : IntEntityClass<Good>(Goods)

    val ID: Int by lazy {
        id.value
    }

    var name by Goods.nameColumn
    var description by Goods.descriptionColumn
    var price by Goods.priceColumn
    var amount by Goods.amountColumn
    var type by Goods.typeColumn
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