package at.fhtw.openscrum.management.application.command

import java.util.UUID

class CreateProjectCommand(
    val projectName: String,
    val productOwnerId: UUID,
    val scrumMasterId: UUID,
    val developerIds: Set<UUID>,
)
