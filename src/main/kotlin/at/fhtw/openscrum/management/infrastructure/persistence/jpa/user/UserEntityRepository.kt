package at.fhtw.openscrum.management.infrastructure.persistence.jpa.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserEntityRepository : JpaRepository<UserEntity, Long> {
    fun existsByEmailAddress(email: String): Boolean

    fun existsByUsername(username: String): Boolean

    fun findByUsername(username: String): UserEntity?

    fun findByUserId(userId: UUID): UserEntity?
}
