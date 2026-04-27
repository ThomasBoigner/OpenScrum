package at.fhtw.openscrum.scrum.application.dtos

import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItem
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemStatus
import java.util.UUID

data class ProductBacklogItemDto(
    val productBacklogItemId: UUID,
    val projectId: UUID,
    val title: String,
    val description: String,
    val status: ProductBacklogItemStatusDto,
) {
    constructor(productBacklogItem: ProductBacklogItem) : this(
        productBacklogItemId = productBacklogItem.productBacklogItemId.productBacklogItemId,
        projectId = productBacklogItem.productBacklogItemId.projectId,
        title = productBacklogItem.title,
        description = productBacklogItem.description,
        when (productBacklogItem.status) {
            ProductBacklogItemStatus.IN_BACKLOG -> ProductBacklogItemStatusDto.IN_BACKLOG
            ProductBacklogItemStatus.COMMITED_TO_SPRINT -> ProductBacklogItemStatusDto.COMMITED_TO_SPRINT
            ProductBacklogItemStatus.DONE -> ProductBacklogItemStatusDto.DONE
        },
    )
}
