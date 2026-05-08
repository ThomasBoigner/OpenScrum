package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.sprint

import at.fhtw.openscrum.scrum.domain.model.sprint.SprintBacklogItem
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintBacklogItemId
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintBacklogItemStatus
import at.fhtw.openscrum.scrum.domain.model.teammember.TeamMemberId
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.util.UUID

@Entity
class SprintBacklogItemEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var projectId: UUID,
    var sprintId: UUID,
    var productBacklogItemId: UUID,
    var title: String,
    var description: String,
    var assignedDeveloperUserId: UUID?,
    var assignedDeveloperProjectId: UUID?,
    @Enumerated(EnumType.STRING)
    var status: SprintBacklogItemStatus,
) {
    constructor(sprintBacklogItem: SprintBacklogItem) : this(
        id = sprintBacklogItem.id,
        projectId = sprintBacklogItem.sprintBacklogItemId.projectId,
        sprintId = sprintBacklogItem.sprintBacklogItemId.sprintId,
        productBacklogItemId = sprintBacklogItem.sprintBacklogItemId.productBacklogItemId,
        title = sprintBacklogItem.title,
        description = sprintBacklogItem.description,
        assignedDeveloperUserId = sprintBacklogItem.assignedDeveloper?.userId,
        assignedDeveloperProjectId = sprintBacklogItem.assignedDeveloper?.projectId,
        status = sprintBacklogItem.status,
    )

    fun toSprintBacklogItem(): SprintBacklogItem =
        SprintBacklogItem(
            id = this.id,
            sprintBacklogItemId =
                SprintBacklogItemId(
                    projectId = this.projectId,
                    sprintId = this.sprintId,
                    productBacklogItemId = this.productBacklogItemId,
                ),
            title = title,
            description = description,
            assignedDeveloper =
                assignedDeveloperUserId?.let {
                    TeamMemberId(
                        userId = this.assignedDeveloperUserId!!,
                        projectId = this.assignedDeveloperProjectId!!,
                    )
                },
            status = status,
        )
}
