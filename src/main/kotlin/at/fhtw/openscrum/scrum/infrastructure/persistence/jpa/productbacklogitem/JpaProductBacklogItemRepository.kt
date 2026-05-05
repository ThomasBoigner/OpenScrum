package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.productbacklogitem

import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItem
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemId
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemRepository
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemStatus
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class JpaProductBacklogItemRepository(
    private val productBacklogItemEntityRepository: ProductBacklogItemEntityRepository,
) : ProductBacklogItemRepository {
    override fun save(productBacklogItem: ProductBacklogItem): ProductBacklogItem {
        val entity = ProductBacklogItemEntity(productBacklogItem)
        productBacklogItemEntityRepository.save(entity)
        return productBacklogItem
    }

    override fun findProductBacklogItemByProductBacklogItemId(productBacklogItemId: ProductBacklogItemId): ProductBacklogItem? {
        TODO("Not yet implemented")
    }

    override fun findProductBacklogItemsByProjectId(projectId: UUID): List<ProductBacklogItem> =
        productBacklogItemEntityRepository
            .findProductBacklogItemEntitiesByProjectId(projectId)
            .map { it.toProductBacklogItem() }

    override fun findProductBacklogItemsByProjectIdAndStatus(
        projectId: UUID,
        status: ProductBacklogItemStatus
    ): List<ProductBacklogItem> {
        TODO("Not yet implemented")
    }
}
