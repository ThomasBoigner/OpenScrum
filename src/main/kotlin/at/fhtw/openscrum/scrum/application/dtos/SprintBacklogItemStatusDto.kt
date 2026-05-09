package at.fhtw.openscrum.scrum.application.dtos

import at.fhtw.openscrum.scrum.domain.model.sprint.SprintBacklogItemStatus

enum class SprintBacklogItemStatusDto(
    val canMoveLeft: Boolean,
    val canMoveRight: Boolean,
    val columnLeft: String?,
    val columnRight: String?,
) {
    TO_DO(false, true, null, "in-progress-column"),
    IN_PROGRESS(true, true, "todo-column", "done-column"),
    DONE(true, false, "in-progress-column", null),
    ;

    companion object {
        fun fromSprintBacklogItemStatus(sprintBacklogItemStatus: SprintBacklogItemStatus): SprintBacklogItemStatusDto =
            when (sprintBacklogItemStatus) {
                SprintBacklogItemStatus.TO_DO -> TO_DO
                SprintBacklogItemStatus.IN_PROGRESS -> IN_PROGRESS
                SprintBacklogItemStatus.DONE -> DONE
            }
    }
}
