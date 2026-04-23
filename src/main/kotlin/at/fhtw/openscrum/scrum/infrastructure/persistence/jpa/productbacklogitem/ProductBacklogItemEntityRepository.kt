package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.productbacklogitem

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductBacklogItemEntityRepository : JpaRepository<ProductBacklogItemEntity, Long>
