package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.teammember

import at.fhtw.openscrum.scrum.domain.model.teammember.TeamMember
import at.fhtw.openscrum.scrum.domain.model.teammember.TeamMemberRepository
import java.util.UUID

class JpaTeamMemberRepository : TeamMemberRepository {
    override fun findByProjectIdAndUserName(
        projectId: UUID,
        userName: String,
    ): TeamMember? {
        TODO("Not yet implemented")
    }
}
