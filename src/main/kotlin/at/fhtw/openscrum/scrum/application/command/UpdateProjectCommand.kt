package at.fhtw.openscrum.scrum.application.command

import java.util.UUID

data class UpdateProjectCommand(
    val projectId: UUID,
    val projectName: String,
)
