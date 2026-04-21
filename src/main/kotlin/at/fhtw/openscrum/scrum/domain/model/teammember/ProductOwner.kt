package at.fhtw.openscrum.scrum.domain.model.teammember

class ProductOwner(
    id: Long? = null,
    teamMemberId: TeamMemberId,
    username: String,
    fullName: FullName,
) : TeamMember(id, teamMemberId, username, fullName) {
    override fun isDeveloper(): Boolean = false

    override fun isScrumMaster(): Boolean = false

    override fun isProductOwner(): Boolean = true
}
