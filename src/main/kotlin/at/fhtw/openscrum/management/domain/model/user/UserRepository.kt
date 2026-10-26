package at.fhtw.openscrum.management.domain.model.user

import at.fhtw.openscrum.management.domain.util.Page

interface UserRepository {
    fun findAll(
        usernameQuery: String? = null,
        page: Int = 0,
        size: Int = 5,
        sortBy: String = "username",
    ): Page<User>

    fun save(user: User): User

    fun existsByUsername(username: String): Boolean

    fun existsByEmailAddress(email: String): Boolean

    fun existsByRole(role: Role): Boolean

    fun findByUsername(username: String): User?

    fun findByUserId(userId: UserId): User?

    fun delete(userId: UserId)
}
