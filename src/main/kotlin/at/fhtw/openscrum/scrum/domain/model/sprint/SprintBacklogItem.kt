package at.fhtw.openscrum.scrum.domain.model.sprint

import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemId
import at.fhtw.openscrum.scrum.domain.model.teammember.TeamMemberId

class SprintBacklogItem(
    val id: Long? = null,
    val sprintBacklogItemId: SprintBacklogItemId,
    val title: String,
    val description: String,
    assignedDeveloper: TeamMemberId? = null,
    status: SprintBacklogItemStatus = SprintBacklogItemStatus.TO_DO,
    val productBacklogItemCommittedEvents: MutableList<ProductBacklogItemCommitted> =
        mutableListOf(
            ProductBacklogItemCommitted(
                ProductBacklogItemId(
                    sprintBacklogItemId.projectId,
                    sprintBacklogItemId.productBacklogItemId,
                ),
            ),
        ),
    val sprintBacklogItemMarkedAsDoneEvents: MutableList<SprintBacklogItemMarkedAsDone> = mutableListOf(),
) {
    var assignedDeveloper: TeamMemberId? = assignedDeveloper
        private set

    var status: SprintBacklogItemStatus = status
        private set

    init {
        require(title.isNotBlank()) { "Title cannot be blank" }
        require(description.isNotBlank()) { "Description cannot be blank" }
    }

    fun move(
        moveDirection: MoveDirection,
        developerId: TeamMemberId,
    ) {
        when (moveDirection) {
            MoveDirection.RIGHT -> {
                when (status) {
                    SprintBacklogItemStatus.TO_DO -> {
                        status = SprintBacklogItemStatus.IN_PROGRESS
                        assignedDeveloper = developerId
                    }

                    SprintBacklogItemStatus.IN_PROGRESS -> {
                        status = SprintBacklogItemStatus.DONE
                        assignedDeveloper = developerId
                        sprintBacklogItemMarkedAsDoneEvents.add(
                            SprintBacklogItemMarkedAsDone(
                                ProductBacklogItemId(
                                    sprintBacklogItemId.projectId,
                                    sprintBacklogItemId.productBacklogItemId,
                                ),
                            ),
                        )
                    }

                    else -> {
                        return
                    }
                }
            }

            MoveDirection.LEFT -> {
                when (status) {
                    SprintBacklogItemStatus.IN_PROGRESS -> {
                        status = SprintBacklogItemStatus.TO_DO
                        assignedDeveloper = null
                    }

                    SprintBacklogItemStatus.DONE -> {
                        status = SprintBacklogItemStatus.IN_PROGRESS
                        assignedDeveloper = developerId
                    }

                    else -> {
                        return
                    }
                }
            }
        }
    }

    override fun toString(): String =
        "SprintBacklogItem(productBacklogItemId=$sprintBacklogItemId, title='$title', description='$description', assignedDeveloper=$assignedDeveloper, status=$status)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SprintBacklogItem

        return sprintBacklogItemId == other.sprintBacklogItemId
    }

    override fun hashCode(): Int = sprintBacklogItemId.hashCode()
}
