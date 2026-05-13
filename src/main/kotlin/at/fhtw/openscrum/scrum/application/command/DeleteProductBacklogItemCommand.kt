package at.fhtw.openscrum.scrum.application.command

import java.util.UUID

class DeleteProductBacklogItemCommand(
    val projectId: UUID,
    val productBacklogItemId: UUID,
)
