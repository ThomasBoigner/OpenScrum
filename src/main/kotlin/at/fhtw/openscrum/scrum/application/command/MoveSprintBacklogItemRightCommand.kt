package at.fhtw.openscrum.scrum.application.command

import java.util.UUID

data class MoveSprintBacklogItemRightCommand(
    val projectId: UUID,
    val sprintId: UUID,
    val productBacklogItemId: UUID,
)
