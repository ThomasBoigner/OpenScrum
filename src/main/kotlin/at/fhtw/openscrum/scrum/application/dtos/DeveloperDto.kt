package at.fhtw.openscrum.scrum.application.dtos

import at.fhtw.openscrum.scrum.domain.model.teammember.Developer
import java.util.UUID

data class DeveloperDto(
    val userId: UUID,
    val projectId: UUID,
    val username: String,
    val firstName: String,
    val lastName: String,
    val fullName: String,
) {
    constructor(developer: Developer) : this(
        userId = developer.teamMemberId.userId,
        projectId = developer.teamMemberId.projectId,
        username = developer.username,
        firstName = developer.fullName.firstName,
        lastName = developer.fullName.lastName,
        fullName = developer.fullName.fullName,
    )
}
