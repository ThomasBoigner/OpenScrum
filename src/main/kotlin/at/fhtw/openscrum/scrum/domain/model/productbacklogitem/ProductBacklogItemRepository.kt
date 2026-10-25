package at.fhtw.openscrum.scrum.domain.model.productbacklogitem

import at.fhtw.openscrum.scrum.domain.util.Page
import java.util.UUID

interface ProductBacklogItemRepository {
    fun save(productBacklogItem: ProductBacklogItem): ProductBacklogItem

    fun findProductBacklogItemByProductBacklogItemId(productBacklogItemId: ProductBacklogItemId): ProductBacklogItem?

    fun findProductBacklogItemsByProjectId(
        projectId: UUID,
        titleQuery: String? = null,
        page: Int = 0,
        size: Int = 5,
        sortBy: String = "id",
    ): Page<ProductBacklogItem>

    fun findProductBacklogItemsByProjectIdAndStatus(
        projectId: UUID,
        status: ProductBacklogItemStatus,
    ): List<ProductBacklogItem>

    fun delete(productBacklogItemId: ProductBacklogItemId)

    fun deleteByProjectId(projectId: UUID)
}
