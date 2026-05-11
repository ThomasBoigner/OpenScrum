package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.productbacklogitem

import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Repository
interface ProductBacklogItemEntityRepository : JpaRepository<ProductBacklogItemEntity, Long> {
    fun findProductBacklogItemEntityByProductBacklogItemId(productBacklogItemId: UUID): ProductBacklogItemEntity?

    fun findProductBacklogItemEntitiesByProjectId(projectId: UUID): List<ProductBacklogItemEntity>

    fun findProductBacklogItemEntitiesByProjectIdAndStatus(
        projectId: UUID,
        status: ProductBacklogItemStatus,
    ): List<ProductBacklogItemEntity>

    @Transactional
    fun deleteByProjectIdAndProductBacklogItemId(
        projectId: UUID,
        productBacklogItemId: UUID,
    )
}
