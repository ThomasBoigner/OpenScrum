package at.fhtw.openscrum.scrum.domain.model.teammember

class ScrumMaster(
    id: Long? = null,
    teamMemberId: TeamMemberId,
    username: String,
    fullName: FullName,
) : TeamMember(id, teamMemberId, username, fullName) {
    override fun isDeveloper(): Boolean = false

    override fun isScrumMaster(): Boolean = true

    override fun isProductOwner(): Boolean = false
}
