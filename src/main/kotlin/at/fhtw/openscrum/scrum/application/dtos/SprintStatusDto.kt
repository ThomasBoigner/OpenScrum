package at.fhtw.openscrum.scrum.application.dtos

import at.fhtw.openscrum.scrum.domain.model.sprint.SprintStatus

enum class SprintStatusDto(
    val displayName: String,
    val isPlanned: Boolean,
    val isFinished: Boolean,
) {
    NOT_PLANNED("Not Planned", false, false),
    IN_PROGRESS("In Progress", true, false),
    COMPLETED("Completed", true, true),
    CANCELLED("Cancelled", true, true),
    ;

    companion object {
        fun fromSprintStatus(sprintStatus: SprintStatus): SprintStatusDto =
            when (sprintStatus) {
                SprintStatus.NOT_PLANNED -> NOT_PLANNED
                SprintStatus.IN_PROGRESS -> IN_PROGRESS
                SprintStatus.COMPLETED -> COMPLETED
                SprintStatus.CANCELLED -> CANCELLED
            }
    }
}
