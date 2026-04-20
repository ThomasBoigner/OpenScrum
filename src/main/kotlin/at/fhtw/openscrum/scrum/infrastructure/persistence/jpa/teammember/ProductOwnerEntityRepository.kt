package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.teammember

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ProductOwnerEntityRepository : JpaRepository<ProductOwnerEntity, Long> {
    fun findByProjectId(projectId: UUID): ProductOwnerEntity?

    fun findByUsername(username: String): ProductOwnerEntity?
}
