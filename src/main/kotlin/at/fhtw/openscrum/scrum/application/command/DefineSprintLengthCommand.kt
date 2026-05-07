package at.fhtw.openscrum.scrum.application.command

import java.util.UUID

data class DefineSprintLengthCommand(
    val projectId: UUID,
    val sprintLength: Long,
)
