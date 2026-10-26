package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.productbacklogitem

import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItem
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemId
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemRepository
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemStatus
import at.fhtw.openscrum.scrum.domain.util.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class JpaProductBacklogItemRepository(
    private val productBacklogItemEntityRepository: ProductBacklogItemEntityRepository,
    private val productBacklogItemEntityMapper: ProductBacklogItemEntityMapper,
) : ProductBacklogItemRepository {
    override fun save(productBacklogItem: ProductBacklogItem): ProductBacklogItem {
        val entity = ProductBacklogItemEntity(productBacklogItem)
        productBacklogItemEntityRepository.save(entity)
        return productBacklogItem
    }

    override fun findProductBacklogItemByProductBacklogItemId(productBacklogItemId: ProductBacklogItemId): ProductBacklogItem? =
        productBacklogItemEntityRepository
            .findProductBacklogItemEntityByProductBacklogItemId(productBacklogItemId.productBacklogItemId)
            ?.toProductBacklogItem()

    override fun findProductBacklogItemsByProjectId(
        projectId: UUID,
        titleQuery: String?,
        page: Int,
        size: Int,
        sortBy: String,
    ): Page<ProductBacklogItem> =
        productBacklogItemEntityMapper.toProductBacklogItemPage(
            productBacklogItemEntityRepository
                .findAllByProjectIdAndTitleContainingIgnoreCase(
                    projectId,
                    titleQuery ?: "",
                    PageRequest.of(page, size, Sort.by(Sort.Order.asc(sortBy))),
                ),
        )

    override fun findProductBacklogItemsByProjectIdAndStatus(
        projectId: UUID,
        status: ProductBacklogItemStatus,
        page: Int,
        size: Int,
        sortBy: String,
    ): Page<ProductBacklogItem> =
        productBacklogItemEntityMapper.toProductBacklogItemPage(
            productBacklogItemEntityRepository
                .findProductBacklogItemEntitiesByProjectIdAndStatus(
                    projectId,
                    status,
                    PageRequest.of(page, size, Sort.by(Sort.Order.asc(sortBy))),
                ),
        )

    override fun delete(productBacklogItemId: ProductBacklogItemId) {
        productBacklogItemEntityRepository.deleteByProjectIdAndProductBacklogItemId(
            productBacklogItemId.projectId,
            productBacklogItemId.productBacklogItemId,
        )
    }

    override fun deleteByProjectId(projectId: UUID) = productBacklogItemEntityRepository.deleteByProjectId(projectId)
}
