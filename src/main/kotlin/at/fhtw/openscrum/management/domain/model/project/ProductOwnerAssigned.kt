package at.fhtw.openscrum.management.domain.model.project

import at.fhtw.openscrum.management.domain.model.user.FullName
import at.fhtw.openscrum.management.domain.model.user.UserId
import org.springframework.modulith.NamedInterface
import java.time.LocalDateTime

@NamedInterface("ProductOwnerAssigned")
data class ProductOwnerAssigned(
    val userId: UserId,
    val username: String,
    val fullName: FullName,
    val occurredOn: LocalDateTime = LocalDateTime.now(),
    val eventVersion: Int = 1,
)
