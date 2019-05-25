package mirea.petshop.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import mirea.petshop.DAO
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.postgresql.util.PGobject
//import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

enum class UserRole { Guest, User, Staff, Manager, Admin }

class PGEnum<T : Enum<T>>(enumTypeName: String, enumValue: T?) : PGobject() {  //user enum for postgres
    init {
        value = enumValue?.name
        type = enumTypeName
    }
}
/*
object EnumTable : Table() {
    val enumColumn = customEnumeration("enumColumn", "FooEnum", {value -> Foo.valueOf(value as String)}, { PGEnum("FooEnum", it)}
}
...
transaction {
    exec("CREATE TYPE UserRole AS ENUM ('Guest', 'User', 'Staff', 'Manager', 'Admin');")
    SchemaUtils.create(EnumTable)
    ...
}*/

@Repository
@Transactional
class UserDAO : DAO<User, UserWrapper> {
    override fun get(id: Int): User? {
        return User.findById(id)
    }

    fun get(login: String): User? {
        val users = User.find { Users.loginColumn eq login }

        return when (users.count()) {
            0 -> null
            1 -> users.first()
            else -> throw Exception("Non-unique user login")
        }
    }

    override fun getAll(): List<UserWrapper> {
        return UserWrapper.all().toList()
    }
}

object Users : IntIdTable("users") {
    val nameColumn = text("name")
    val loginColumn = text("login").uniqueIndex()
    val hashColumn = text("hash")
    val saltColumn = text("salt")
    val roleColumn = customEnumeration("role", "UserRole", { value -> UserRole.valueOf(value as String) }, { PGEnum("userrole", it) })
}

@JsonIgnoreProperties("writeValues", "readValues", "_readValues", "db", "klass")
class User(id: EntityID<Int>) : IntEntity(id) {
    companion object Table : IntEntityClass<User>(Users)

    val ID: Int by lazy {
        id.value
    }

    var name by Users.nameColumn
    var login by Users.loginColumn
    var hash by Users.hashColumn
    var salt by Users.saltColumn
    var role by Users.roleColumn
}

@JsonIgnoreProperties("writeValues", "readValues", "_readValues", "db", "klass")
class UserWrapper(id: EntityID<Int>) : IntEntity(id) {
    companion object Table : IntEntityClass<UserWrapper>(Users)

    val ID: Int by lazy {
        id.value
    }

    var name by Users.nameColumn
    var role by Users.roleColumn
}