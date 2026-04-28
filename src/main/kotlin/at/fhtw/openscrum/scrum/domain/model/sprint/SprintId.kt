package at.fhtw.openscrum.scrum.domain.model.sprint

import java.util.UUID

data class SprintId(
    val projectId: UUID,
    val sprintId: UUID = UUID.randomUUID(),
)
