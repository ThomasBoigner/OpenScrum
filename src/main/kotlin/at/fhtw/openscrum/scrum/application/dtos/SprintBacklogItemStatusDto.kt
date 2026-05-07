package at.fhtw.openscrum.scrum.application.dtos

import at.fhtw.openscrum.scrum.domain.model.sprint.SprintBacklogItemStatus

enum class SprintBacklogItemStatusDto {
    TO_DO,
    IN_PROGRESS,
    DONE,
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
