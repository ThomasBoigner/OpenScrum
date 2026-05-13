package at.fhtw.openscrum.scrum.application.command

import java.util.UUID

data class DeleteSprintBacklogItemsCommand(
    val projectId: UUID,
    val productBacklogItemId: UUID,
)
