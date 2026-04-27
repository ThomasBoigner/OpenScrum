package at.fhtw.openscrum.scrum.application.command

import java.util.UUID

data class DefineProductBacklogItemCommand(
    val projectId: UUID,
    val title: String,
    val description: String,
)
