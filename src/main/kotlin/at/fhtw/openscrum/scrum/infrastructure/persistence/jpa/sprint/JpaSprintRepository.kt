package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.sprint

import at.fhtw.openscrum.scrum.domain.model.sprint.Sprint
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class JpaSprintRepository(
    private val sprintEntityRepository: SprintEntityRepository,
) : SprintRepository {
    override fun save(sprint: Sprint): Sprint = sprintEntityRepository.save(SprintEntity(sprint)).toSprint()

    override fun findSprintsByProjectId(projectId: UUID): List<Sprint> =
        sprintEntityRepository.findByProjectId(projectId).map { it.toSprint() }

    override fun countByProjectId(projectId: UUID): Int = sprintEntityRepository.countByProjectId(projectId)
}
