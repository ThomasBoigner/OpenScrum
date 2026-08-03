package at.fhtw.openscrum.management.application.command

import java.util.UUID

data class DemoteUserCommand(
    val userId: UUID,
)
