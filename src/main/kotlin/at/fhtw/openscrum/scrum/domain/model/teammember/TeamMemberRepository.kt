package at.fhtw.openscrum.scrum.domain.model.teammember

import java.util.UUID

interface TeamMemberRepository {
    fun findByProjectIdAndUsername(
        projectId: UUID,
        userName: String,
    ): TeamMember?

    fun findAllByUserId(userId: UUID): List<TeamMember>
}
