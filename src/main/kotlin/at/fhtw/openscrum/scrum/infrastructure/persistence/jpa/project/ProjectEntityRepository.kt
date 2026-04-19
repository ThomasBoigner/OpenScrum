package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.project

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository("scrumProjectEntityRepository")
interface ProjectEntityRepository : JpaRepository<ProjectEntity, Long> {
    fun findByProjectId(projectId: UUID): ProjectEntity?
}
