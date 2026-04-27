package at.fhtw.openscrum.scrum.domain.model.productbacklogitem

import at.fhtw.openscrum.scrum.domain.model.teammember.ProductOwner
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.UUID

class ProductBacklogItemService(
    private val productBacklogItemRepository: ProductBacklogItemRepository,
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
}
