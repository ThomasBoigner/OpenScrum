package at.fhtw.openscrum.scrum.application.dtos

import at.fhtw.openscrum.scrum.domain.model.teammember.ScrumMaster
import java.util.UUID

data class ScrumMasterDto(
    val userId: UUID,
    val projectId: UUID,
    val username: String,
    val firstName: String,
    val lastName: String,
    val fullName: String,
) {
    constructor(scrumMaster: ScrumMaster) : this(
        userId = scrumMaster.teamMemberId.userId,
        projectId = scrumMaster.teamMemberId.projectId,
        username = scrumMaster.username,
        firstName = scrumMaster.fullName.firstName,
        lastName = scrumMaster.fullName.lastName,
        fullName = scrumMaster.fullName.fullName,
    )
}
