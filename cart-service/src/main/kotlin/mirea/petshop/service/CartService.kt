package mirea.petshop.service

import mirea.petshop.model.Cart
import mirea.petshop.model.CartDAO
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CartService @Autowired constructor(val cartDAO: CartDAO) {
    val logger = LoggerFactory.getLogger("AuthLogger")

    fun getCart(userID: Int): Cart {
        return cartDAO.get(userID)
    }

    fun getHistory(userID: Int): Cart {
        return cartDAO.get(userID, false)
    }

    fun getAll(): Array<Cart> {
        return cartDAO.getAll().toTypedArray()
    }

    fun setItem(userID: Int, itemID: Int, amt: Int): Boolean {
        when (amt) {
            0 -> cartDAO.delete(userID, itemID)
            else -> {
                try {
                    cartDAO.save(userID, itemID, amt)
                } catch (e: Exception) {
                    logger.info(e.message)
                    cartDAO.update(userID, itemID, amt)
                }
            }
        }
        return true
    }

    fun checkout(userID: Int): Boolean {
        //GlobalScope.launch {}
        val cart = getCart(userID)
        if (cart.purchases.isEmpty())
            return false

        return if (confirmPayment()) {
            cartDAO.complete(userID)
            true
        } else
            false
    }

    fun confirmPayment(): Boolean = true
}