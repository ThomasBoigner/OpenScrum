package at.fhtw.openscrum.scrum.domain.model.teammember

import java.util.UUID

data class TeamMemberId(
    val userId: UUID,
    val projectId: UUID,
)
