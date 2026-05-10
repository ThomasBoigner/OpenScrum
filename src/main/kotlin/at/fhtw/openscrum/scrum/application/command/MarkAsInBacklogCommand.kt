package at.fhtw.openscrum.scrum.application.command

import java.util.UUID

data class MarkAsInBacklogCommand(
    val projectId: UUID,
    val productBacklogItemId: UUID,
)
