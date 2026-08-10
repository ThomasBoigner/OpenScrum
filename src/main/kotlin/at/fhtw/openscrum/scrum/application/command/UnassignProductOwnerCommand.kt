package at.fhtw.openscrum.scrum.application.command

import java.util.UUID

data class UnassignProductOwnerCommand(
    val userId: UUID,
    val projectId: UUID,
)
