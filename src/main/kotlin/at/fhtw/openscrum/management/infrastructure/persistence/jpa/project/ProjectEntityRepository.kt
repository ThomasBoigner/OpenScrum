package at.fhtw.openscrum.management.infrastructure.persistence.jpa.project

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository("managementProjectEntityRepository")
interface ProjectEntityRepository : JpaRepository<ProjectEntity, Long> {
    fun existsByProjectName(projectName: String): Boolean

    @Query(
        value = "SELECT * FROM management_project_entity WHERE scrum_master_id = :userId OR product_owner_id = :userId OR :userId = ANY(developer_ids)",
        nativeQuery = true,
    )
    fun findProjectsOfUser(userId: UUID): List<ProjectEntity>
}
