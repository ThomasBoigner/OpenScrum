package at.fhtw.openscrum.scrum.application.command

import java.util.UUID

data class CreateProjectCommand(
    val projectId: UUID,
    val projectName: String,
)
