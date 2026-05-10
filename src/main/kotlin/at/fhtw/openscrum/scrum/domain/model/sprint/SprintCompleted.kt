package at.fhtw.openscrum.scrum.domain.model.sprint

import java.time.LocalDateTime

data class SprintCompleted(
    val sprintId: SprintId,
    val occurredOn: LocalDateTime = LocalDateTime.now(),
    val eventVersion: Int = 1,
)
