package at.fhtw.openscrum.management.infrastructure.persistence.jpa.user

import at.fhtw.openscrum.management.domain.model.user.Role
import at.fhtw.openscrum.management.domain.model.user.User
import at.fhtw.openscrum.management.domain.model.user.UserId
import at.fhtw.openscrum.management.domain.model.user.UserRepository
import at.fhtw.openscrum.management.domain.util.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository

@Repository
class JpaUserRepository(
    private val userEntityRepository: UserEntityRepository,
    private val userEntityMapper: UserEntityMapper,
) : UserRepository {
    override fun findAll(
        usernameQuery: String?,
        page: Int,
        size: Int,
        sortBy: String,
    ): Page<User> =
        userEntityMapper.toUserPage(
            userEntityRepository
                .findAllByUsernameContainingIgnoreCase(
                    usernameQuery ?: "",
                    PageRequest.of(page, size, Sort.by(Sort.Order.asc(sortBy))),
                ),
        )

    override fun save(user: User): User {
        val userEntity = UserEntity(user)
        userEntityRepository.save(userEntity)
        return user
    }

    override fun existsByUsername(username: String): Boolean = userEntityRepository.existsByUsername(username)

    override fun existsByEmailAddress(email: String): Boolean = userEntityRepository.existsByEmailAddress(email)

    override fun existsByRole(role: Role): Boolean = userEntityRepository.existsByRole(role)

    override fun findByUsername(username: String): User? = userEntityRepository.findByUsername(username)?.toUser()

    override fun findByUserId(userId: UserId): User? = userEntityRepository.findByUserId(userId.token)?.toUser()

    override fun delete(userId: UserId) = userEntityRepository.deleteByUserId(userId.token)
}
