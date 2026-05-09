package at.fhtw.openscrum.scrum.domain.model.sprint

import java.util.UUID

data class SprintBacklogItemId(
    val projectId: UUID,
    val sprintId: UUID,
    val productBacklogItemId: UUID,
)
