package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.teammember

import at.fhtw.openscrum.scrum.domain.model.teammember.Developer
import at.fhtw.openscrum.scrum.domain.model.teammember.FullName
import at.fhtw.openscrum.scrum.domain.model.teammember.TeamMember
import at.fhtw.openscrum.scrum.domain.model.teammember.TeamMemberId
import jakarta.persistence.Entity
import java.util.UUID

@Entity
class DeveloperEntity(
    id: Long? = null,
    userId: UUID,
    projectId: UUID,
    username: String,
    firstName: String,
    lastName: String,
) : TeamMemberEntity(id, userId, projectId, username, firstName, lastName) {
    constructor(developer: Developer) : this(
        id = developer.id,
        userId = developer.teamMemberId.userId,
        projectId = developer.teamMemberId.projectId,
        username = developer.username,
        firstName = developer.fullName.firstName,
        lastName = developer.fullName.lastName,
    )

    constructor() : this(
        id = null,
        userId = UUID.randomUUID(),
        projectId = UUID.randomUUID(),
        username = "",
        firstName = "",
        lastName = "",
    )

    fun toDeveloper(): Developer =
        Developer(
            id = this.id,
            teamMemberId = TeamMemberId(userId = userId, projectId = projectId),
            username = username,
            fullName = FullName(firstName = firstName, lastName = lastName),
        )

    override fun toTeamMember(): TeamMember =
        Developer(
            id = this.id,
            teamMemberId = TeamMemberId(userId = userId, projectId = projectId),
            username = username,
            fullName = FullName(firstName = firstName, lastName = lastName),
        )
}
