package at.fhtw.openscrum.scrum.application.command

import java.util.UUID

class CreateProjectCommand(
    val projectId: UUID,
    val projectName: String,
)
