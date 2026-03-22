package at.fhtw.openscrum.management.domain.model.user

interface UserRepository {
    fun findAll(): List<User>

    fun save(user: User): User

    fun existsByUsername(username: String): Boolean

    fun existsByEmailAddress(email: String): Boolean

    fun findByUsername(username: String): User?
}
