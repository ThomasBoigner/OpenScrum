package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.sprint

import at.fhtw.openscrum.scrum.domain.model.sprint.SprintId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SprintEntityRepository : JpaRepository<SprintEntity, Long> {
    fun findByProjectId(projectId: UUID): List<SprintEntity>

    fun findByProjectIdAndSprintId(
        projectId: UUID,
        sprintId: UUID,
    ): SprintEntity?

    fun countByProjectId(projectId: UUID): Int
}
