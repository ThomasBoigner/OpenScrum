package at.fhtw.openscrum.scrum.application.dtos

import at.fhtw.openscrum.scrum.domain.model.sprint.Sprint
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintStatus
import java.time.LocalDate
import java.util.UUID

data class SprintDto(
    val sprintId: UUID,
    val projectId: UUID,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val status: SprintStatusDto,
) {
    constructor(sprint: Sprint) : this(
        sprintId = sprint.sprintId.sprintId,
        projectId = sprint.sprintId.projectId,
        startDate = sprint.startDate,
        endDate = sprint.endDate,
        status =
            when (sprint.status) {
                SprintStatus.NOT_PLANNED -> SprintStatusDto.NOT_PLANNED
                SprintStatus.IN_PROGRESS -> SprintStatusDto.IN_PROGRESS
                SprintStatus.COMPLETED -> SprintStatusDto.COMPLETED
            },
    )
}
