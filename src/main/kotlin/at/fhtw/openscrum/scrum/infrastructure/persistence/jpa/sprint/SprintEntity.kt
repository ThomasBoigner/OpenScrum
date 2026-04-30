package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.sprint

import at.fhtw.openscrum.scrum.domain.model.sprint.Sprint
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintId
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintStatus
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDate
import java.util.UUID

@Entity
class SprintEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var projectId: UUID,
    var sprintId: UUID,
    var sprintName: String,
    var startDate: LocalDate,
    var endDate: LocalDate,
    @Enumerated(EnumType.STRING)
    var status: SprintStatus,
    var sprintGoal: String?,
) {
    constructor(sprint: Sprint) : this(
        id = sprint.id,
        projectId = sprint.sprintId.projectId,
        sprintId = sprint.sprintId.sprintId,
        sprintName = sprint.sprintName,
        startDate = sprint.startDate,
        endDate = sprint.endDate,
        status = sprint.status,
        sprintGoal = sprint.sprintGoal,
    )

    fun toSprint(): Sprint =
        Sprint(
            id = id,
            sprintId = SprintId(projectId = projectId, sprintId = sprintId),
            sprintName = sprintName,
            startDate = startDate,
            endDate = endDate,
            status = status,
            sprintGoal = sprintGoal,
        )
}
