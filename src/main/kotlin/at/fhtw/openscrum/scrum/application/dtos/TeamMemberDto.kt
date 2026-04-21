package at.fhtw.openscrum.scrum.application.dtos

import at.fhtw.openscrum.scrum.domain.model.teammember.TeamMember
import java.util.UUID

data class TeamMemberDto(
    val userId: UUID,
    val projectId: UUID,
    val username: String,
    val firstName: String,
    val lastName: String,
    val fullName: String,
    val isDeveloper: Boolean,
    val isScrumMaster: Boolean,
    val isProductOwner: Boolean,
) {
    constructor(teamMember: TeamMember) : this(
        userId = teamMember.teamMemberId.userId,
        projectId = teamMember.teamMemberId.projectId,
        username = teamMember.username,
        firstName = teamMember.fullName.firstName,
        lastName = teamMember.fullName.lastName,
        fullName = teamMember.fullName.fullName,
        isDeveloper = teamMember.isDeveloper(),
        isScrumMaster = teamMember.isScrumMaster(),
        isProductOwner = teamMember.isProductOwner(),
    )
}
