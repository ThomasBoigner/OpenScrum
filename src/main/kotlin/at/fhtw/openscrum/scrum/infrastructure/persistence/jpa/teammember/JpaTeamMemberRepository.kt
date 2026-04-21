package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.teammember

import at.fhtw.openscrum.scrum.domain.model.teammember.TeamMember
import at.fhtw.openscrum.scrum.domain.model.teammember.TeamMemberRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class JpaTeamMemberRepository(
    private val teamMemberEntityRepository: TeamMemberEntityRepository,
) : TeamMemberRepository {
    override fun findByProjectIdAndUsername(
        projectId: UUID,
        userName: String,
    ): TeamMember? = teamMemberEntityRepository.findByProjectIdAndUsername(projectId, userName)?.toTeamMember()
}
