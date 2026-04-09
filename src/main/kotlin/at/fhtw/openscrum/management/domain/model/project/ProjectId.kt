package at.fhtw.openscrum.management.domain.model.project

import java.util.UUID

data class ProjectId(
    val token: UUID = UUID.randomUUID(),
)
