package at.fhtw.openscrum.scrum.application.command

import java.util.UUID

data class AssignDeveloperCommand(
    val userId: UUID,
    val projectId: UUID,
    val username: String,
    val firstName: String,
    val lastName: String,
)
