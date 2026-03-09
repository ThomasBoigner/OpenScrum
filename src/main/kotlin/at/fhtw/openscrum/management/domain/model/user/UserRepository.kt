package at.fhtw.openscrum.management.domain.model.user

interface UserRepository {
    fun save(user: User): User

    fun existsByUsername(username: String): Boolean

    fun existsByEmail(email: String): Boolean
}
