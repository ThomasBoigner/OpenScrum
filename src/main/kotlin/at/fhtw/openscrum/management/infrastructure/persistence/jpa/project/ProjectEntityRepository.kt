package at.fhtw.openscrum.management.infrastructure.persistence.jpa.project

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository("managementProjectEntityRepository")
interface ProjectEntityRepository : JpaRepository<ProjectEntity, Long> {
    fun existsByProjectName(projectName: String): Boolean

    fun findByScrumMasterIdOrProductOwnerIdOrDeveloperIdsContaining(
        scrumMasterId: UUID,
        productOwnerId: UUID,
        developerId: UUID,
    ): List<ProjectEntity>
}
