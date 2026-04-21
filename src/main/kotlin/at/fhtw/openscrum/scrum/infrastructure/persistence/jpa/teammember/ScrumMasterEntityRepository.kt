package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.teammember

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ScrumMasterEntityRepository : JpaRepository<ScrumMasterEntity, Long> {
    fun findByProjectId(projectId: UUID): ScrumMasterEntity?

    fun findByProjectIdAndUsername(
        projectId: UUID,
        username: String,
    ): ScrumMasterEntity?
}
