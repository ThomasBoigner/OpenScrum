package at.fhtw.openscrum.scrum.domain.model.teammember

class Developer(
    id: Long? = null,
    teamMemberId: TeamMemberId,
    username: String,
    fullName: FullName,
) : TeamMember(id, teamMemberId, username, fullName) {
    override fun isDeveloper(): Boolean = true

    override fun isScrumMaster(): Boolean = false

    override fun isProductOwner(): Boolean = true
}
