package at.fhtw.openscrum.scrum.domain.model.teammember

open class TeamMember(
    val id: Long? = null,
    val teamMemberId: TeamMemberId,
    val username: String,
    val fullName: FullName,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TeamMember

        return teamMemberId == other.teamMemberId
    }

    override fun hashCode(): Int = teamMemberId.hashCode()

    override fun toString(): String = "TeamMember(fullName=$fullName, username='$username', teamMemberId=$teamMemberId)"
}
