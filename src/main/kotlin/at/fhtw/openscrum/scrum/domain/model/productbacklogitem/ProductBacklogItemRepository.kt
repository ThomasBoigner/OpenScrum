package at.fhtw.openscrum.scrum.domain.model.productbacklogitem

import java.util.UUID

interface ProductBacklogItemRepository {
    fun save(productBacklogItem: ProductBacklogItem): ProductBacklogItem

    fun findProductBacklogItemByProductBacklogItemId(productBacklogItemId: ProductBacklogItemId): ProductBacklogItem?

    fun findProductBacklogItemsByProjectId(projectId: UUID): List<ProductBacklogItem>
}
