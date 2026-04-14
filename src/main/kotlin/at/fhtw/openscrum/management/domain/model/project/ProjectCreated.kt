package at.fhtw.openscrum.management.domain.model.project

import java.time.LocalDateTime

data class ProjectCreated(
    val projectId: ProjectId,
    val projectName: String,
    val occurredOn: LocalDateTime = LocalDateTime.now(),
    val eventVersion: Int = 1,
)
