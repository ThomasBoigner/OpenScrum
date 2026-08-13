package at.fhtw.openscrum.management.application.command

import java.util.UUID

data class UpdateUserCommand(
    val userId: UUID,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val password: String,
)
