package at.fhtw.openscrum.scrum.domain.model.sprint

import at.fhtw.openscrum.scrum.domain.util.Page
import java.time.LocalDate
import java.util.UUID

interface SprintRepository {
    fun save(sprint: Sprint): Sprint

    fun findSprintsByProjectId(projectId: UUID): List<Sprint>

    fun findSprintsByProjectId(
        projectId: UUID,
        sprintNameQuery: String? = null,
        page: Int = 0,
        size: Int = 5,
        sortBy: String = "sprintName",
    ): Page<Sprint>

    fun findSprintBySprintId(sprintId: SprintId): Sprint?

    fun findSprintsByEndDateBeforeAndStatusInProgressOrStatusNotPlanned(endDate: LocalDate): List<Sprint>

    fun countByProjectId(projectId: UUID): Int

    fun deleteByProjectId(projectId: UUID)
}
