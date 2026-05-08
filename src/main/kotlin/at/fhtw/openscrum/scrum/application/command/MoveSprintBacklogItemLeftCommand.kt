package at.fhtw.openscrum.scrum.application.command

import java.util.UUID

data class MoveSprintBacklogItemLeftCommand(
    val projectId: UUID,
    val sprintId: UUID,
    val productBacklogItemId: UUID,
)
