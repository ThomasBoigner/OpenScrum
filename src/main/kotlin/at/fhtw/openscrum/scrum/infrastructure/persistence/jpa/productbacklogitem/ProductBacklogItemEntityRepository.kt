package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.productbacklogitem

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ProductBacklogItemEntityRepository : JpaRepository<ProductBacklogItemEntity, Long> {
    fun findProductBacklogItemEntitiesByProjectId(projectId: UUID): List<ProductBacklogItemEntity>
}
