package at.fhtw.openscrum.scrum.application.dtos

import at.fhtw.openscrum.scrum.domain.model.sprint.SprintBacklogItemStatus

enum class SprintBacklogItemStatusDto(
    val canMoveLeft: Boolean,
    val canMoveRight: Boolean,
) {
    TO_DO(false, true),
    IN_PROGRESS(true, true),
    DONE(true, false),
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
