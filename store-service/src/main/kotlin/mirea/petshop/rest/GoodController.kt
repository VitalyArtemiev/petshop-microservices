package mirea.petshop.rest

import mirea.petshop.model.Good
import mirea.petshop.model.GoodWrapper
import mirea.petshop.service.StoreService
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.concurrent.atomic.AtomicLong

typealias Token = String

@RestController
@RequestMapping("")
class GoodController @Autowired constructor(val storeService: StoreService) {
    @GetMapping("")
    fun getAll(): ResponseEntity<Array<GoodWrapper>> {
        return transaction{
            ResponseEntity(storeService.getAllGoods(), HttpStatus.OK)
        }
    }

    @GetMapping("/{goodID}")
    fun getByID(@PathVariable("goodID") goodID: Int): ResponseEntity<Good> {
        return transaction {
            val good = storeService.getGood(goodID)

            if (good == null) {
                ResponseEntity(HttpStatus.NOT_FOUND)
            }
            else{
                ResponseEntity(good, HttpStatus.OK)
            }
        }
    }

    @PutMapping("/{goodID}")
    fun addStock(@PathVariable goodID: Int, @RequestParam amount: Int): ResponseEntity<Good> {
        return if (storeService.updateGood(goodID, amount)) {
            ResponseEntity(HttpStatus.OK)
        } else
            ResponseEntity(HttpStatus.NOT_FOUND)
    }
}