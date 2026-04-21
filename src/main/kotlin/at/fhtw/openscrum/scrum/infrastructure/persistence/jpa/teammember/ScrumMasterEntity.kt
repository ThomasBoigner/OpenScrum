package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.teammember

import at.fhtw.openscrum.scrum.domain.model.teammember.FullName
import at.fhtw.openscrum.scrum.domain.model.teammember.ScrumMaster
import at.fhtw.openscrum.scrum.domain.model.teammember.TeamMember
import at.fhtw.openscrum.scrum.domain.model.teammember.TeamMemberId
import jakarta.persistence.Entity
import java.util.UUID

@Entity
class ScrumMasterEntity(
    id: Long? = null,
    userId: UUID,
    projectId: UUID,
    username: String,
    firstName: String,
    lastName: String,
) : TeamMemberEntity(id, userId, projectId, username, firstName, lastName) {
    constructor(scrumMaster: ScrumMaster) : this(
        id = scrumMaster.id,
        userId = scrumMaster.teamMemberId.userId,
        projectId = scrumMaster.teamMemberId.projectId,
        username = scrumMaster.username,
        firstName = scrumMaster.fullName.firstName,
        lastName = scrumMaster.fullName.lastName,
    )

    fun toScrumMaster(): ScrumMaster =
        ScrumMaster(
            id = id,
            teamMemberId = TeamMemberId(userId = userId, projectId = projectId),
            username = username,
            fullName = FullName(firstName = firstName, lastName = lastName),
        )

    override fun toTeamMember(): TeamMember =
        ScrumMaster(
            id = id,
            teamMemberId = TeamMemberId(userId = userId, projectId = projectId),
            username = username,
            fullName = FullName(firstName = firstName, lastName = lastName),
        )
}
