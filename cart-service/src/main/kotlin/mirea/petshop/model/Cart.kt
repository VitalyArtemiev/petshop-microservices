package mirea.petshop.model

import mirea.petshop.model.Purchases.amount
import mirea.petshop.model.Purchases.goodID
import mirea.petshop.model.Purchases.userID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Repository
@Transactional
class CartDAO {
    fun get(userID: Int, pending: Boolean = true): Cart {
        val purchases = transaction {
            addLogger(StdOutSqlLogger)
            Purchases.select { (Purchases.userID eq userID) and (Purchases.pending eq pending) }.map {
                Purchase(GoodWrapper.findById(it[goodID])!!, it[amount])
            }.toTypedArray()
        }

        var total = BigDecimal.ZERO

        for (p in purchases) {
            total += p.good.price * p.amount.toBigDecimal()
        }

        return Cart(purchases, total)
    }

    fun getAll(): List<Cart> {
        val result = mutableListOf<Cart>()

        transaction {
            val userIDs = Purchases.slice(userID).selectAll().withDistinct().map { it[userID] }

            for (id in userIDs) {
                result.add(get(id))
            }
        }

        return result.toList()
    }

    fun save(userID: Int, goodID: Int, amount: Int) {
        transaction {
            addLogger(StdOutSqlLogger)
            Purchases.insert {
                it[this.userID] = userID
                it[this.goodID] = goodID
                it[this.amount] = amount
                it[pending] = true
            }
        }
    }

    fun update(userID: Int, goodID: Int, amt: Int) {
        transaction {
            addLogger(StdOutSqlLogger)
            if (amt > 0) {
                Purchases.update({ (Purchases.userID eq userID) and (Purchases.goodID eq goodID) }) {
                    it[amount] = amt
                }
            } else
                Purchases.deleteWhere { (Purchases.userID eq userID) and (Purchases.goodID eq goodID) }
        }
    }

    fun complete(userID: Int) {
        transaction {
            addLogger(StdOutSqlLogger)
            Purchases.update({ Purchases.userID eq userID }) {
                it[pending] = false
            }
        }
    }

    fun delete(userID: Int, goodID: Int) {
        transaction {
            addLogger(StdOutSqlLogger)
            Purchases.deleteWhere { (Purchases.userID eq userID) and (Purchases.goodID eq goodID) }
        }
    }
}

object Purchases : Table("purchases") {
    //val id = integer("id").autoIncrement().primaryKey()
    val userID = integer("user_id").primaryKey(0)
    val goodID = integer("good_id").primaryKey(1)
    val amount = integer("amount")
    val pending = bool("pending").primaryKey(2)
}

class Purchase(val good: GoodWrapper, val amount: Int)

class Cart(val purchases: Array<Purchase>, val total: BigDecimal)

/*@JsonIgnoreProperties("writeValues", "readValues", "_readValues", "db", "klass")
class Purchase: Entity() {
    companion object Table : IntEntityClass<Good>(Goods)

    val userID by Purchases.userID
    val goodID = integer("good_id")
    val amount = integer("amount")
    val pending = bool("pending")
}*/