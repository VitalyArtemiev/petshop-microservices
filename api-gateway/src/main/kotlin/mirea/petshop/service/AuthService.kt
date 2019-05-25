package mirea.petshop.service

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import mirea.petshop.model.User
import mirea.petshop.model.UserRole
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.util.*

typealias Token = String

@Component("authService")
class AuthService @Autowired constructor(val restTemplate: RestTemplate) {
    //@Value("\${auth.secret}")
    val secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)
    @Value("\${auth.tokenDuration}")
    val tokenDuration = 60
    @Value("\${auth.tokenSkew}")
    val tokenSkew: Long = 60

    val logger = LoggerFactory.getLogger("AuthLogger")

    /*fun createJWT(user: User, durationDays: Long): String {

    }

    fun verifyToken(token: String): Token {

    }

    //@Throws(UserAlreadyExistsException::class)
    fun registerUser(nameColumn: String, balance: String, hash: String) {

    }

    //@Throws(AuthFailedException::class)
    fun authUser(hash: String, salt: String, nameColumn: String): String {

    }

    //@Throws(TokenOutOfDateException::class, AuthFailedException::class)
    fun checkToken(AToken: String): User {

    }

    //@Throws(TokenOutOfDateException::class, AuthFailedException::class)
    fun refreshToken(RToken: String): String {

    }*/

    fun revokeToken() {

    }

    fun hasRole(role: UserRole, token: Token?): Boolean {

        val jws: Jws<Claims>

        try {
            jws = Jwts.parser()
                    .setAllowedClockSkewSeconds(tokenSkew) //expiration handled automatically
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)


            logger.debug(jws.body["role"] as String)
            val tokenRole = UserRole.valueOf(jws.body["role"] as String)

            if (tokenRole >= role) {
                return true
            }
            // we can safely trust the JWT
        } catch (e: JwtException) {       // (4)
            revokeToken()
            logger.debug(e.message)

            // we *cannot* use the JWT as intended by its creator
        }

        return false
    }

    private fun getExpirationTimeSecondsInTheFuture(seconds: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.add(Calendar.SECOND, seconds)
        return calendar.time
    }

    /*fun registerUser(login: String, password: String, role: UserRole, name: String): Boolean {
        if (login.isBlank() || password.isBlank())
            return false

        val salt = BCrypt.gensalt()
        val hash = BCrypt.hashpw(password, salt)

        restTemplate.

        userService.saveUser(login, salt, hash, name, role)

        return true
    }*/

    fun authUser(user: User, password: String): Token? {
        return if (BCrypt.checkpw(password, user.hash)) {
            var token = Jwts.builder()
                    .setSubject(user.id.toString())
                    .claim("role", user.role)
                    .setIssuedAt(Date())
                    .setExpiration(getExpirationTimeSecondsInTheFuture(tokenDuration))
                    .signWith(secretKey).compact()

            token

        } else {
            null
        }
    }

    fun getUserID(token: Token): Int? {
        val jws: Jws<Claims>

        return try {
            jws = Jwts.parser()
                    .setAllowedClockSkewSeconds(tokenSkew) //expiration handled automatically
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)

            jws.body.subject.toInt()
        } catch (e: JwtException) {
            revokeToken()
            logger.debug(e.message)

            null
        }

    }
}