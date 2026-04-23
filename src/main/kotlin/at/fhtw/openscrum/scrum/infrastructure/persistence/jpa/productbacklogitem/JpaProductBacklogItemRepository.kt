package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.productbacklogitem

import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItem
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemRepository
import org.springframework.stereotype.Repository

@Repository
class JpaProductBacklogItemRepository(
    private val productBacklogItemEntityRepository: ProductBacklogItemEntityRepository,
) : ProductBacklogItemRepository {
    override fun save(productBacklogItem: ProductBacklogItem): ProductBacklogItem {
        val entity = ProductBacklogItemEntity(productBacklogItem)
        productBacklogItemEntityRepository.save(entity)
        return productBacklogItem
    }
}
