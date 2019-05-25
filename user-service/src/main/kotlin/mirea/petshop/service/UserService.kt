package mirea.petshop.service

import mirea.petshop.model.User
import mirea.petshop.model.UserDAO
import mirea.petshop.model.UserRole
import mirea.petshop.model.UserWrapper
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.stereotype.Component

@Component
class UserService @Autowired constructor(val userDao: UserDAO) {

    fun saveUser(login: String, salt: String, hash: String, name: String, role: UserRole) {
        transaction {
            User.new {
                this.login = login
                this.salt = salt
                this.hash = hash
                this.role = role
                this.name = name
            }
        }
    }

    fun getAll(): Array<UserWrapper> {
        return userDao.getAll().toTypedArray()
    }

    fun getUser(id: Int): User? {
        return userDao.get(id)
    }

    fun getUser(login: String): User? {
        return userDao.get(login)
    }

    fun registerUser(login: String, password: String, role: UserRole, name: String): Boolean {
        if (login.isBlank() || password.isBlank())
            return false

        val salt = BCrypt.gensalt()
        val hash = BCrypt.hashpw(password, salt)
        saveUser(login, salt, hash, name, role)

        return true
    }
}