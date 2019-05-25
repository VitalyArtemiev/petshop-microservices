package mirea.petshop.rest

import mirea.petshop.model.*
import mirea.petshop.service.AuthService
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForEntity
import java.util.concurrent.atomic.AtomicLong

typealias Token = String

data class Greeting(val id: Long, val content: String)

@RestController
@RequestMapping("/")
class HelloController {
    val counter = AtomicLong()

    @GetMapping("/greeting", "")
    fun greeting(@RequestParam(value = "nameColumn", defaultValue = "World") name: String) =
            Greeting(counter.incrementAndGet(), "Hello, $name")
}

@RestController
@RequestMapping("goods")
class GoodController @Autowired constructor(val restTemplate: RestTemplate) {
    @Value("\${ports.goods}")
    val port = ""

    @GetMapping("")
    fun getAll(): ResponseEntity<Array<GoodWrapper>> {

        return restTemplate.getForEntity(port, Array<GoodWrapper>::class.java)
    }

    @GetMapping("/{goodID}")
    fun getByID(@PathVariable goodID: Int): ResponseEntity<Any> {
        return restTemplate.getForEntity("$port/$goodID", Any::class.java)
    }

    @PutMapping("/{goodID}")
    @PreAuthorize("@authService.hasRole({'Staff'}, #token)")
    fun addStock(@PathVariable goodID: Int, @RequestParam amount: Int,
                 @RequestHeader("auth") token: Token): ResponseEntity<Any> {
        return restTemplate.exchange("$port/$goodID", HttpMethod.PUT, null, Any::class.java)
    }
}

@RestController
@RequestMapping("users")
class UserController @Autowired constructor(val restTemplate: RestTemplate, val authService: AuthService) {
    @Value("\${ports.users}")
    val port = ""

    @GetMapping("")
    @PreAuthorize("@authService.hasRole({'Admin'}, #token)")
    fun getAll(@RequestHeader("auth") token: Token): ResponseEntity<Any> {
        return restTemplate.getForEntity(port, Any::class.java)
    }

    @GetMapping("/{userID}")
    @PreAuthorize("@authService.hasRole({'Admin'}, #token)")
    fun getByID(@PathVariable userID: Int, @RequestHeader("auth") token: Token): ResponseEntity<Any> {
        return restTemplate.getForEntity("$port/$userID", Any::class.java)
    }

    @PostMapping("")
    fun login(@RequestParam login: String, @RequestParam password: String): ResponseEntity<Token> {

        //return restTemplate.exchange("$port!login=$login&password=$password", HttpMethod.POST, null, Any::class.java)

        val user =
            restTemplate.getForObject("$port?login=$login", User::class.java)
        /*}
        catch (e: HttpClientErrorException) {

            null
        }*/

        return if (user == null)
            ResponseEntity(HttpStatus.NOT_FOUND)
        else {
            val token = authService.authUser(user, password)
            if (token == null)
                ResponseEntity(HttpStatus.FORBIDDEN)
            else
                ResponseEntity(token, HttpStatus.OK)
        }
    }

    @PutMapping("")
    fun register(@RequestParam("login") login: String, @RequestParam("password") password: String,
                 @RequestParam("role", defaultValue = "User") role: UserRole,
                 @RequestHeader("auth", defaultValue = "") token: Token): ResponseEntity<Any> {
        if (role > UserRole.User) {
            if (!authService.hasRole(UserRole.Admin, token))
                return ResponseEntity(HttpStatus.UNAUTHORIZED)
        }

        return restTemplate.exchange("$port?login=$login&password=$password&role=$role", HttpMethod.PUT, null, Any::class.java)
    }
}

@RestController
@RequestMapping("/cart")
@PreAuthorize("@authService.hasRole({'User'}, #token)")
class CartController @Autowired constructor(val restTemplate: RestTemplate, val authService: AuthService) {
    @Value("\${ports.carts}")
    val port = ""

    @GetMapping
    fun getCartContents(@RequestHeader("auth") token: Token): ResponseEntity<Cart> {
        val userID = authService.getUserID(token) ?: return ResponseEntity(HttpStatus.BAD_REQUEST)
        val cart = restTemplate.getForObject("$port/$userID", Cart::class.java)
        return ResponseEntity(cart, HttpStatus.OK)
    }

    @GetMapping("/all")
    @PreAuthorize("@authService.hasRole({'Admin'}, #token)")
    fun getAll(@RequestHeader("auth") token: Token): ResponseEntity<Array<Cart>> {
        val carts = restTemplate.getForObject("$port", Array<Cart>::class.java)
        return ResponseEntity(carts, HttpStatus.OK)
    }

    @PostMapping
    fun checkout(@RequestHeader("auth") token: Token): ResponseEntity<String> {
        val userID = authService.getUserID(token) ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        return restTemplate.exchange("$port/$userID", HttpMethod.POST, null, String::class.java)
    }

    @DeleteMapping
    fun removeItem(@RequestParam goodID: Int, @RequestHeader("auth") token: Token): ResponseEntity<String> {
        val userID = authService.getUserID(token) ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        return restTemplate.exchange("$port/$userID?goodID=$goodID", HttpMethod.DELETE, null, String::class.java)
    }

    @PutMapping
    fun updateItem(@RequestParam goodID: Int, @RequestParam amount: Int,
                   @RequestHeader("auth") token: Token): ResponseEntity<String> {
        val userID = authService.getUserID(token) ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        return restTemplate.exchange("$port/$userID?goodID=$goodID&amount=$amount", HttpMethod.PUT, null, String::class.java)
    }
}

