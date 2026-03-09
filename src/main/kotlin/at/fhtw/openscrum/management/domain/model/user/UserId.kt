package at.fhtw.openscrum.management.domain.model.user

import java.util.UUID

data class UserId(
    val token: UUID = UUID.randomUUID(),
)
