package mirea.petshop.rest

import mirea.petshop.model.User
import mirea.petshop.model.UserRole
import mirea.petshop.model.UserWrapper
import mirea.petshop.service.UserService
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

typealias Token = String


@RestController
class UserController @Autowired constructor(val userService: UserService) {
    @GetMapping("")
    fun getAll(): ResponseEntity<Array<UserWrapper>> {
        return transaction {
            ResponseEntity(userService.getAll(), HttpStatus.OK)
        }
    }

    @GetMapping("", params = ["login"])
    fun getByLogin(@RequestParam login: String): ResponseEntity<User> {
        return transaction {
            val user = userService.getUser(login)

            if (user == null) {
                ResponseEntity(HttpStatus.NOT_FOUND)
            } else {
                ResponseEntity(user, HttpStatus.OK)
            }
        }
    }

    @GetMapping("/{userID}")
    fun getByID(@PathVariable userID: Int): ResponseEntity<User> {
        return transaction {
            val user = userService.getUser(userID)

            if (user == null) {
                ResponseEntity(HttpStatus.NOT_FOUND)
            } else {
                ResponseEntity(user, HttpStatus.OK)
            }
        }
    }

    /*@PostMapping("")
    fun login(@RequestParam login: String, @RequestParam password: String): ResponseEntity<Token> {
        return transaction {
            val user = userService.getUser(login)
            if (user == null)
                ResponseEntity<Token>(HttpStatus.NOT_FOUND)
            else {
                val token = authService.authUser(user, password)
                if (token == null)
                    ResponseEntity<Token>(HttpStatus.FORBIDDEN)
                else
                    ResponseEntity(token, HttpStatus.OK)
            }
        }
    }*/

    @PutMapping("")
    fun register(@RequestParam("login") login: String, @RequestParam("password") password: String,
                 @RequestParam("role", defaultValue = "User") role: UserRole): ResponseEntity<Token> {


        return if (transaction { userService.getUser(login) != null })
            ResponseEntity(HttpStatus.CONFLICT)
        else {
            var name = "Joe Shmuck"
            if (userService.registerUser(login, password, role, name))
                ResponseEntity(HttpStatus.CREATED)
            else
                ResponseEntity(HttpStatus.NOT_ACCEPTABLE)
        }
    }
}