package at.fhtw.openscrum.management.infrastructure.persistence.jpa.user

import at.fhtw.openscrum.management.domain.model.user.User
import at.fhtw.openscrum.management.domain.model.user.UserId
import at.fhtw.openscrum.management.domain.model.user.UserRepository
import org.springframework.stereotype.Repository

@Repository
class JpaUserRepository(
    private val userEntityRepository: UserEntityRepository,
) : UserRepository {
    override fun findAll(): List<User> = userEntityRepository.findAll().map { it.toUser() }

    override fun save(user: User): User {
        val userEntity = UserEntity(user)
        userEntityRepository.save(userEntity)
        return user
    }

    override fun existsByUsername(username: String): Boolean = userEntityRepository.existsByUsername(username)

    override fun existsByEmailAddress(email: String): Boolean = userEntityRepository.existsByEmailAddress(email)

    override fun findByUsername(username: String): User? = userEntityRepository.findByUsername(username)?.toUser()

    override fun findByUserId(userId: UserId): User? = userEntityRepository.findByUserId(userId.token)?.toUser()

    override fun delete(userId: UserId) = userEntityRepository.deleteByUserId(userId.token)
}
