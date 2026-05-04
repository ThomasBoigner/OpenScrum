package at.fhtw.openscrum.scrum.domain.model.sprint

import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemId
import at.fhtw.openscrum.scrum.domain.model.teammember.TeamMemberId

class SprintBacklogItem(
    val productBacklogItemId: ProductBacklogItemId,
    val title: String,
    val description: String,
    assignedDeveloper: TeamMemberId? = null,
    status: SprintBacklogItemStatus = SprintBacklogItemStatus.TO_DO,
) {
    var assignedDeveloper: TeamMemberId? = assignedDeveloper
        private set

    var status: SprintBacklogItemStatus = status
        private set

    init {
        require(title.isNotBlank()) { "Title cannot be blank" }
        require(description.isNotBlank()) { "Description cannot be blank" }
    }

    override fun toString(): String =
        "SprintBacklogItem(productBacklogItemId=$productBacklogItemId, title='$title', description='$description', assignedDeveloper=$assignedDeveloper, status=$status)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SprintBacklogItem

        return productBacklogItemId == other.productBacklogItemId
    }

    override fun hashCode(): Int = productBacklogItemId.hashCode()
}
