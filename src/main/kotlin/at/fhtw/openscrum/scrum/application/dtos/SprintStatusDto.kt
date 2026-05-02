package at.fhtw.openscrum.scrum.application.dtos

import at.fhtw.openscrum.scrum.domain.model.sprint.SprintStatus

enum class SprintStatusDto(
    val displayName: String,
) {
    NOT_PLANNED("Not Planned"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled"),
    ;

    companion object {
        fun fromSprintStatus(sprintStatus: SprintStatus): SprintStatusDto =
            when (sprintStatus) {
                SprintStatus.NOT_PLANNED -> SprintStatusDto.NOT_PLANNED
                SprintStatus.IN_PROGRESS -> SprintStatusDto.IN_PROGRESS
                SprintStatus.COMPLETED -> SprintStatusDto.COMPLETED
                SprintStatus.CANCELLED -> SprintStatusDto.CANCELLED
            }
    }
}
