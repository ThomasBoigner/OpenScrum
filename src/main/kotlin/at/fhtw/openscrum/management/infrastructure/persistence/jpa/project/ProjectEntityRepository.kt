package at.fhtw.openscrum.management.infrastructure.persistence.jpa.project

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectEntityRepository : JpaRepository<ProjectEntity, Long> {
    fun existsByProjectName(projectName: String): Boolean
}
