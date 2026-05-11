package at.fhtw.openscrum.scrum.application.command

import java.util.UUID

data class CancelSprintCommand(
    val projectId: UUID,
    val sprintId: UUID,
)
