package at.fhtw.openscrum.management.domain.model.project

import at.fhtw.openscrum.management.domain.model.user.UserId
import org.springframework.modulith.NamedInterface
import java.time.LocalDateTime

@NamedInterface("ScrumMasterUnassigned")
data class ScrumMasterUnassigned(
    val userId: UserId,
    val projectId: ProjectId,
    val occurredOn: LocalDateTime = LocalDateTime.now(),
    val eventVersion: Int = 1,
)
