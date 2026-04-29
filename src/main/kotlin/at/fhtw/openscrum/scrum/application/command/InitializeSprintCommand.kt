package at.fhtw.openscrum.scrum.application.command

import java.util.UUID

data class InitializeSprintCommand(
    val projectId: UUID,
    val sprintLength: Long,
)
