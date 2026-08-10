package at.fhtw.openscrum.scrum.application.command

import java.util.UUID

data class UnassignDeveloperCommand(
    val userId: UUID,
    val projectId: UUID,
)
