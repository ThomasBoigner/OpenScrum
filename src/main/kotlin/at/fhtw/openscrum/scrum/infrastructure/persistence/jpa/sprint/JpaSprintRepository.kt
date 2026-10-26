package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.sprint

import at.fhtw.openscrum.scrum.domain.model.sprint.Sprint
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintId
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintRepository
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintStatus
import at.fhtw.openscrum.scrum.domain.util.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.UUID

@Repository
class JpaSprintRepository(
    private val sprintEntityRepository: SprintEntityRepository,
    private val sprintEntityMapper: SprintEntityMapper,
) : SprintRepository {
    override fun save(sprint: Sprint): Sprint = sprintEntityRepository.save(SprintEntity(sprint)).toSprint()

    override fun findSprintsByProjectId(projectId: UUID): List<Sprint> =
        sprintEntityRepository.findByProjectId(projectId).map { it.toSprint() }

    override fun findSprintsByProjectId(
        projectId: UUID,
        sprintNameQuery: String?,
        page: Int,
        size: Int,
        sortBy: String,
    ): Page<Sprint> =
        sprintEntityMapper.toSprintPage(
            sprintEntityRepository.findAllByProjectIdAndSprintNameContainingIgnoreCase(
                projectId,
                sprintNameQuery ?: "",
                PageRequest.of(page, size, Sort.by(Sort.Order.desc(sortBy))),
            ),
        )

    override fun findSprintBySprintId(sprintId: SprintId): Sprint? =
        sprintEntityRepository.findByProjectIdAndSprintId(sprintId.projectId, sprintId.sprintId)?.toSprint()

    override fun findSprintsByEndDateBeforeAndStatusInProgressOrStatusNotPlanned(endDate: LocalDate): List<Sprint> =
        sprintEntityRepository
            .findByEndDateBeforeAndStatusOrStatus(endDate, SprintStatus.IN_PROGRESS, SprintStatus.NOT_PLANNED)
            .map { it.toSprint() }

    override fun countByProjectId(projectId: UUID): Int = sprintEntityRepository.countByProjectId(projectId)

    override fun deleteByProjectId(projectId: UUID) = sprintEntityRepository.deleteAll(sprintEntityRepository.findByProjectId(projectId))
}
