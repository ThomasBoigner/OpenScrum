package at.fhtw.openscrum.scrum.domain.model.teammember

import java.util.UUID

interface TeamMemberRepository {
    fun findByProjectIdAndUserName(
        projectId: UUID,
        userName: String,
    ): TeamMember?
}
