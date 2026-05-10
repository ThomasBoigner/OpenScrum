package at.fhtw.openscrum.scrum.application.command

import java.util.UUID

data class UncommitFromSprintCommand(
    val projectId: UUID,
    val productBacklogItemId: UUID,
)
