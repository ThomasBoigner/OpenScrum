package at.fhtw.openscrum.scrum.domain.model.teammember

class ScrumMaster(
    id: Long? = null,
    teamMemberId: TeamMemberId,
    username: String,
    fullName: FullName,
) : TeamMember(id, teamMemberId, username, fullName)
