package mirea.petshop.rest

import mirea.petshop.model.Cart
import mirea.petshop.service.CartService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

typealias Token = String

@RestController
class CartController @Autowired constructor(val cartService: CartService) {

    @GetMapping("/{userID}")
    fun getCartContents(@PathVariable userID: Int): ResponseEntity<Cart> {
        return ResponseEntity(cartService.getCart(userID), HttpStatus.OK)
    }

    @GetMapping
    fun getAll(): ResponseEntity<Array<Cart>> {
        return ResponseEntity(cartService.getAll(), HttpStatus.OK)
    }

    @PostMapping("/{userID}")
    fun checkout(@PathVariable userID: Int): ResponseEntity<String> {
        return if (cartService.checkout(userID))
            ResponseEntity(HttpStatus.OK)
        else
            ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @DeleteMapping("/{userID}")
    fun removeItem(@PathVariable userID: Int, @RequestParam goodID: Int): ResponseEntity<String> {
        return if (cartService.setItem(userID, goodID, 0))
            ResponseEntity(HttpStatus.OK)
        else
            ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @PutMapping("/{userID}")
    fun updateItem(@PathVariable userID: Int, @RequestParam goodID: Int, @RequestParam amount: Int): ResponseEntity<String> {
        return if (cartService.setItem(userID, goodID, amount))
            ResponseEntity(HttpStatus.OK)
        else
            ResponseEntity(HttpStatus.NOT_FOUND)
    }
}