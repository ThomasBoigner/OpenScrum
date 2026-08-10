package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.teammember

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Repository
interface ProductOwnerEntityRepository : JpaRepository<ProductOwnerEntity, Long> {
    fun findByProjectId(projectId: UUID): ProductOwnerEntity?

    fun findByProjectIdAndUsername(
        projectId: UUID,
        username: String,
    ): ProductOwnerEntity?

    @Transactional
    fun deleteByUserIdAndProjectId(
        userId: UUID,
        projectId: UUID,
    )
}
