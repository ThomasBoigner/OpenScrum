package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.sprint

import at.fhtw.openscrum.scrum.domain.model.sprint.SprintStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.UUID

@Repository
interface SprintEntityRepository : JpaRepository<SprintEntity, Long> {
    fun findByProjectId(projectId: UUID): List<SprintEntity>

    fun findByProjectIdAndSprintId(
        projectId: UUID,
        sprintId: UUID,
    ): SprintEntity?

    fun findByEndDateBeforeAndStatusOrStatus(
        endDate: LocalDate,
        status1: SprintStatus,
        status2: SprintStatus,
    ): List<SprintEntity>

    fun countByProjectId(projectId: UUID): Int
}
