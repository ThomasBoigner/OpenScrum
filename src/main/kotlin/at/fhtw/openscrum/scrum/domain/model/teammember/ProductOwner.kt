package at.fhtw.openscrum.scrum.domain.model.teammember

class ProductOwner(
    teamMemberId: TeamMemberId,
    username: String,
    fullName: FullName,
) : TeamMember(teamMemberId, username, fullName)
