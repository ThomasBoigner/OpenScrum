package at.fhtw.openscrum.scrum.application.dtos

import at.fhtw.openscrum.scrum.domain.model.sprint.Sprint
import java.time.LocalDate
import java.util.UUID

data class SprintDto(
    val sprintId: UUID,
    val projectId: UUID,
    val sprintName: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val status: SprintStatusDto,
    val sprintGoal: String?,
    val numberOfSprintBacklogItems: Int,
) {
    constructor(sprint: Sprint) : this(
        sprintId = sprint.sprintId.sprintId,
        projectId = sprint.sprintId.projectId,
        sprintName = sprint.sprintName,
        startDate = sprint.startDate,
        endDate = sprint.endDate,
        status = SprintStatusDto.fromSprintStatus(sprint.status),
        sprintGoal = sprint.sprintGoal,
        numberOfSprintBacklogItems = sprint.sprintBacklogItems.size,
    )
}
