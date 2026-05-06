package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.teammember

import at.fhtw.openscrum.scrum.domain.model.teammember.Developer
import at.fhtw.openscrum.scrum.domain.model.teammember.DeveloperRepository
import at.fhtw.openscrum.scrum.domain.model.teammember.TeamMemberId
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class JpaDeveloperRepository(
    private val developerEntityRepository: DeveloperEntityRepository,
) : DeveloperRepository {
    override fun save(developer: Developer): Developer {
        val developerEntity = DeveloperEntity(developer)
        developerEntityRepository.save(developerEntity)
        return developer
    }

    override fun findByProjectId(projectId: UUID): List<Developer> =
        developerEntityRepository.findAllByProjectId(projectId).map { it.toDeveloper() }

    override fun findByTeamMemberId(teamMemberId: TeamMemberId): Developer? =
        developerEntityRepository.findByUserIdAndProjectId(teamMemberId.userId, teamMemberId.projectId)?.toDeveloper()
}
