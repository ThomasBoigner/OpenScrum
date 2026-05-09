package at.fhtw.openscrum.scrum.application.command

import at.fhtw.openscrum.scrum.domain.model.sprint.MoveDirection
import java.util.UUID

data class MoveSprintBacklogItemCommand(
    val projectId: UUID,
    val sprintId: UUID,
    val productBacklogItemId: UUID,
    val moveDirection: MoveDirection,
)
