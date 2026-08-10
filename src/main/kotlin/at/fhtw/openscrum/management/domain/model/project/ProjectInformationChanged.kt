package at.fhtw.openscrum.management.domain.model.project

import org.springframework.modulith.NamedInterface
import java.time.LocalDateTime

@NamedInterface("ProjectInformationChanged")
data class ProjectInformationChanged(
    val projectId: ProjectId,
    val projectName: String,
    val occurredOn: LocalDateTime = LocalDateTime.now(),
    val eventVersion: Int = 1,
)
