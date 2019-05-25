package mirea.petshop.model

import java.math.BigDecimal

class Purchase(val good: GoodWrapper, val amount: Int)

class Cart(val purchases: Array<Purchase>, val total: BigDecimal)

