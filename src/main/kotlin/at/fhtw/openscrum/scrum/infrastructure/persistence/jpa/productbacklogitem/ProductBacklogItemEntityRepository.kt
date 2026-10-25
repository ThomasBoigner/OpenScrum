package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.productbacklogitem

import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Repository
interface ProductBacklogItemEntityRepository : JpaRepository<ProductBacklogItemEntity, Long> {
    fun findProductBacklogItemEntityByProductBacklogItemId(productBacklogItemId: UUID): ProductBacklogItemEntity?

    fun findAllByProjectIdAndTitleContainingIgnoreCase(
        projectId: UUID,
        title: String,
        pageable: Pageable,
    ): Page<ProductBacklogItemEntity>

    fun findProductBacklogItemEntitiesByProjectIdAndStatus(
        projectId: UUID,
        status: ProductBacklogItemStatus,
        pageable: Pageable,
    ): Page<ProductBacklogItemEntity>

    @Transactional
    fun deleteByProjectIdAndProductBacklogItemId(
        projectId: UUID,
        productBacklogItemId: UUID,
    )

    @Transactional
    fun deleteByProjectId(projectId: UUID)
}
