package at.fhtw.openscrum.scrum.application.dtos

import at.fhtw.openscrum.scrum.domain.model.sprint.SprintBacklogItem
import at.fhtw.openscrum.scrum.domain.model.teammember.TeamMember
import java.util.UUID

data class SprintBacklogItemDto(
    val projectId: UUID,
    val productBacklogItemId: UUID,
    val title: String,
    val description: String,
    val assignedDeveloperUserId: UUID?,
    val assignedDeveloperProjectId: TeamMemberDto?,
    val status: SprintBacklogItemStatusDto,
) {
    constructor(sprintBacklogItem: SprintBacklogItem, teamMember: TeamMember?) : this(
        projectId = sprintBacklogItem.productBacklogItemId.projectId,
        productBacklogItemId = sprintBacklogItem.productBacklogItemId.productBacklogItemId,
        title = sprintBacklogItem.title,
        description = sprintBacklogItem.description,
        assignedDeveloperUserId = sprintBacklogItem.assignedDeveloper?.userId,
        assignedDeveloperProjectId = teamMember?.let { TeamMemberDto(it) },
        status = SprintBacklogItemStatusDto.fromSprintBacklogItemStatus(sprintBacklogItem.status),
    )
}
