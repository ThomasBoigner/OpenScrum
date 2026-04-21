package at.fhtw.openscrum.management.infrastructure.persistence.jpa.user

import at.fhtw.openscrum.management.domain.model.user.EmailAddress
import at.fhtw.openscrum.management.domain.model.user.Role
import at.fhtw.openscrum.management.domain.model.user.User
import at.fhtw.openscrum.management.domain.model.user.UserId
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.springframework.data.domain.AbstractAggregateRoot
import java.util.UUID

@Entity
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val userId: UUID,
    val username: String,
    val emailAddress: String,
    @Embedded
    val fullName: FullNameEmbeddable,
    val password: String,
    val role: Role,
) : AbstractAggregateRoot<UserEntity>() {
    constructor(user: User) : this(
        id = user.id,
        userId = user.userId.token,
        username = user.username,
        emailAddress = user.emailAddress.emailAddress,
        fullName = FullNameEmbeddable(user.fullName),
        password = user.password,
        role = user.role,
    )

    fun toUser(): User =
        User(
            id = id,
            userId = UserId(userId),
            username = username,
            emailAddress = EmailAddress(emailAddress),
            fullName = fullName.toFullName(),
            password = password,
            role = role,
        )
}
