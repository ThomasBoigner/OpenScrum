package at.fhtw.openscrum.scrum.application.dtos

import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemStatus

enum class ProductBacklogItemStatusDto(
    val displayName: String,
) {
    IN_BACKLOG("In backlog"),
    COMMITED_TO_SPRINT("Commited to sprint"),
    DONE("Done"),
    ;

    companion object {
        fun fromProductBacklogItemStatus(productBacklogItemStatus: ProductBacklogItemStatus): ProductBacklogItemStatusDto =
            when (productBacklogItemStatus) {
                ProductBacklogItemStatus.IN_BACKLOG -> ProductBacklogItemStatusDto.IN_BACKLOG
                ProductBacklogItemStatus.COMMITED_TO_SPRINT -> ProductBacklogItemStatusDto.COMMITED_TO_SPRINT
                ProductBacklogItemStatus.DONE -> ProductBacklogItemStatusDto.DONE
            }
    }
}
