package at.fhtw.openscrum.scrum.application.dtos

import at.fhtw.openscrum.scrum.domain.model.sprint.SprintBacklogItem
import at.fhtw.openscrum.scrum.domain.model.teammember.TeamMember
import java.util.UUID

data class SprintBacklogItemDto(
    val projectId: UUID,
    val sprintId: UUID,
    val productBacklogItemId: UUID,
    val title: String,
    val description: String,
    val assignedDeveloper: TeamMemberDto?,
    val status: SprintBacklogItemStatusDto,
) {
    constructor(sprintBacklogItem: SprintBacklogItem, teamMember: TeamMember?) : this(
        projectId = sprintBacklogItem.sprintBacklogItemId.projectId,
        sprintId = sprintBacklogItem.sprintBacklogItemId.sprintId,
        productBacklogItemId = sprintBacklogItem.sprintBacklogItemId.productBacklogItemId,
        title = sprintBacklogItem.title,
        description = sprintBacklogItem.description,
        assignedDeveloper = teamMember?.let { TeamMemberDto(it) },
        status = SprintBacklogItemStatusDto.fromSprintBacklogItemStatus(sprintBacklogItem.status),
    )
}
