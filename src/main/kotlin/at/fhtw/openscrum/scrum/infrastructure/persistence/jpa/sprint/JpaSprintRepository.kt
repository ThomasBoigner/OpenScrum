package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.sprint

import at.fhtw.openscrum.scrum.domain.model.sprint.Sprint
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintId
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintRepository
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintStatus
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.UUID

@Repository
class JpaSprintRepository(
    private val sprintEntityRepository: SprintEntityRepository,
) : SprintRepository {
    override fun save(sprint: Sprint): Sprint = sprintEntityRepository.save(SprintEntity(sprint)).toSprint()

    override fun findSprintsByProjectId(projectId: UUID): List<Sprint> =
        sprintEntityRepository.findByProjectId(projectId).map { it.toSprint() }

    override fun findSprintBySprintId(sprintId: SprintId): Sprint? =
        sprintEntityRepository.findByProjectIdAndSprintId(sprintId.projectId, sprintId.sprintId)?.toSprint()

    override fun findSprintsByEndDateBeforeAndStatusInProgressOrStatusNotPlanned(endDate: LocalDate): List<Sprint> =
        sprintEntityRepository
            .findByEndDateBeforeAndStatusOrStatus(endDate, SprintStatus.IN_PROGRESS, SprintStatus.NOT_PLANNED)
            .map { it.toSprint() }

    override fun countByProjectId(projectId: UUID): Int = sprintEntityRepository.countByProjectId(projectId)
}
