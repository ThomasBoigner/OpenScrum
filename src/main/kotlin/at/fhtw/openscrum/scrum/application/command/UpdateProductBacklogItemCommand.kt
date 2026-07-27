package at.fhtw.openscrum.scrum.application.command

import java.util.UUID

data class UpdateProductBacklogItemCommand(
    val projectId: UUID,
    val productBacklogItemId: UUID,
    val title: String,
    val description: String,
)
