package at.fhtw.openscrum.scrum.application.dtos

import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemStatus

enum class ProductBacklogItemStatusDto(
    val displayName: String,
) {
    IN_BACKLOG("In backlog"),
    COMMITED_TO_SPRINT("Committed to sprint"),
    COMMITED_TO_SPRINT_DONE("Commited to sprint (done)"),
    DONE("Done"),
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
