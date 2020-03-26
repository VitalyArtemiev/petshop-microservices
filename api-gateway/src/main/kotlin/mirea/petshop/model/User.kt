package mirea.petshop.model

enum class UserRole { Guest, User, Staff, Manager, Admin }

data class User(val id: Int, var name: String, var login: String, var hash: String, var salt: String, var role: UserRole)