package at.fhtw.openscrum.management.domain.model.user

import org.springframework.modulith.NamedInterface
import java.time.LocalDateTime

@NamedInterface("UserInformationChanged")
data class UserInformationChanged(
    val userId: UserId,
    val username: String,
    val emailAddress: EmailAddress,
    val fullName: FullName,
    val occurredOn: LocalDateTime = LocalDateTime.now(),
    val eventVersion: Int = 1,
)
