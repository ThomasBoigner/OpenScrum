package at.fhtw.openscrum.scrum.application.dtos

import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemStatus

enum class ProductBacklogItemStatusDto(
    val displayName: String,
    val isCommitedToSprint: Boolean,
) {
    IN_BACKLOG("In backlog", false),
    COMMITED_TO_SPRINT("Committed to sprint", true),
    COMMITED_TO_SPRINT_DONE("Committed to sprint (done)", true),
    DONE("Done", false),
    ;

    companion object {
        fun fromProductBacklogItemStatus(productBacklogItemStatus: ProductBacklogItemStatus): ProductBacklogItemStatusDto =
            when (productBacklogItemStatus) {
                ProductBacklogItemStatus.IN_BACKLOG -> IN_BACKLOG
                ProductBacklogItemStatus.COMMITTED_TO_SPRINT -> COMMITED_TO_SPRINT
                ProductBacklogItemStatus.COMMITTED_TO_SPRINT_DONE -> COMMITED_TO_SPRINT_DONE
                ProductBacklogItemStatus.DONE -> DONE
            }
    }
}
