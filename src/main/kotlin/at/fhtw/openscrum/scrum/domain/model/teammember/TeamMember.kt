package at.fhtw.openscrum.scrum.domain.model.teammember

abstract class TeamMember(
    val id: Long? = null,
    val teamMemberId: TeamMemberId,
    val username: String,
    val fullName: FullName,
) {
    abstract fun isDeveloper(): Boolean

    abstract fun isScrumMaster(): Boolean

    abstract fun isProductOwner(): Boolean

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TeamMember

        return teamMemberId == other.teamMemberId
    }

    override fun hashCode(): Int = teamMemberId.hashCode()

    override fun toString(): String = "TeamMember(fullName=$fullName, username='$username', teamMemberId=$teamMemberId)"
}
