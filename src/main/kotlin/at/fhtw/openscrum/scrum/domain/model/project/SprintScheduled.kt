package at.fhtw.openscrum.scrum.domain.model.project

import java.time.LocalDateTime

data class SprintScheduled(
    val projectId: ProjectId,
    val sprintLength: SprintLength,
    val occurredOn: LocalDateTime = LocalDateTime.now(),
    val eventVersion: Int = 1,
)