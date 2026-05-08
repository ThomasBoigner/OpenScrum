package at.fhtw.openscrum.scrum.application.dtos

import at.fhtw.openscrum.scrum.domain.model.sprint.SprintBacklogItemStatus

enum class SprintBacklogItemStatusDto(
    val canMoveLeft: Boolean,
    val canMoveRight: Boolean,
    val columnId: String,
) {
    TO_DO(false, true, "todo-column"),
    IN_PROGRESS(true, true, "in-progress-column"),
    DONE(true, false, "done-column"),
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
