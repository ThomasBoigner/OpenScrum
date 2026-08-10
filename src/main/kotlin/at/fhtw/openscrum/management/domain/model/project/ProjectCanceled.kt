package at.fhtw.openscrum.management.domain.model.project

import org.springframework.modulith.NamedInterface
import java.time.LocalDateTime

@NamedInterface("ProjectCanceled")
data class ProjectCanceled(
    val projectId: ProjectId,
    val occurredOn: LocalDateTime = LocalDateTime.now(),
    val eventVersion: Int = 1,
)
