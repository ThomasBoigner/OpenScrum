package at.fhtw.openscrum.scrum.domain.model.teammember

class Developer(
    teamMemberId: TeamMemberId,
    username: String,
    fullName: FullName,
) : TeamMember(teamMemberId, username, fullName)
