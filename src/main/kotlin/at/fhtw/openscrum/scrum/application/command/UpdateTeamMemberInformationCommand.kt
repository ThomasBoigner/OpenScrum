package at.fhtw.openscrum.scrum.application.command

import java.util.UUID

data class UpdateTeamMemberInformationCommand(
    val userId: UUID,
    val username: String,
    val firstName: String,
    val lastName: String,
)
