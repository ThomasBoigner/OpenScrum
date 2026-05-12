package at.fhtw.openscrum.scrum.domain.model.productbacklogitem

import at.fhtw.openscrum.scrum.domain.model.EventPublisher
import at.fhtw.openscrum.scrum.domain.model.teammember.ProductOwner
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.UUID

class ProductBacklogItemService(
    private val productBacklogItemRepository: ProductBacklogItemRepository,
    private val eventPublisher: EventPublisher,
    private val log: Logger = LoggerFactory.getLogger(ProductBacklogItemService::class.java),
) {
    fun defineBacklogItem(
        productOwner: ProductOwner?,
        projectId: UUID,
        title: String,
        description: String,
    ): ProductBacklogItem {
        log.debug("Trying to define product backlog item {}", title)
        require(productOwner?.teamMemberId?.projectId == projectId) { "You are not the product owner of this project!" }

        val productBacklogItem =
            ProductBacklogItem(
                productBacklogItemId = ProductBacklogItemId(projectId = projectId),
                title = title,
                description = description,
            )
        log.info("Defined product backlog item {}", productBacklogItem)
        return productBacklogItemRepository.save(productBacklogItem)
    }

    fun deleteProductBacklogItem(
        productOwner: ProductOwner?,
        productBacklogItem: ProductBacklogItem,
    ) {
        log.debug("Trying to delete product backlog item {}", productBacklogItem)
        require(productOwner?.teamMemberId?.projectId == productBacklogItem.productBacklogItemId.projectId) {
            "You are not the product owner of this project!"
        }
        require(!productBacklogItem.status.isCommitedToSprint) { "Cannot delete a product backlog item that is committed to a sprint!" }

        productBacklogItemRepository.delete(productBacklogItem.productBacklogItemId)
        eventPublisher.publishEvent(ProductBacklogItemDeleted(productBacklogItem.productBacklogItemId))
        log.info("Deleted product backlog item {}", productBacklogItem)
    }
}
