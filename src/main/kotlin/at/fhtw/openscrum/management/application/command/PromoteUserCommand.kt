package at.fhtw.openscrum.management.application.command

import java.util.UUID

data class PromoteUserCommand(
    val userId: UUID,
)
