package at.fhtw.openscrum.scrum.domain.model.teammember

import java.util.UUID

interface DeveloperRepository {
    fun save(developer: Developer): Developer

    fun findByProjectId(projectId: UUID): List<Developer>

    fun findByTeamMemberId(teamMemberId: TeamMemberId): Developer?
}
