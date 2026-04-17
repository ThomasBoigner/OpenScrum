package at.fhtw.openscrum.scrum.domain.model.teammember

class Developer(
    id: Long? = null,
    teamMemberId: TeamMemberId,
    username: String,
    fullName: FullName,
) : TeamMember(id, teamMemberId, username, fullName)
