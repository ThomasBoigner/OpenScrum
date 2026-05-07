package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.teammember

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface DeveloperEntityRepository : JpaRepository<DeveloperEntity, Long> {
    fun findAllByProjectId(projectId: UUID): List<DeveloperEntity>

    fun findByUserIdAndProjectId(
        userId: UUID,
        projectId: UUID,
    ): DeveloperEntity?
}
