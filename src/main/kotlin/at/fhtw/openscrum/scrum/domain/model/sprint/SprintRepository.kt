package at.fhtw.openscrum.scrum.domain.model.sprint

import java.util.UUID

interface SprintRepository {
    fun save(sprint: Sprint): Sprint

    fun findSprintsByProjectId(projectId: UUID): List<Sprint>
}
