package at.fhtw.openscrum.scrum.application.command

import java.util.UUID

data class CancelProjectCommand(
    val projectId: UUID,
)
