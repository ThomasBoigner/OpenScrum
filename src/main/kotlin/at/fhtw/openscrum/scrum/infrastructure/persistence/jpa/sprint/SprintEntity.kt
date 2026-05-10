package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.sprint

import at.fhtw.openscrum.scrum.domain.model.sprint.Sprint
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintId
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintStatus
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import org.springframework.data.domain.AbstractAggregateRoot
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
    @OneToMany(cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE], fetch = FetchType.EAGER)
    var sprintBacklogItems: MutableSet<SprintBacklogItemEntity> = mutableSetOf(),
) : AbstractAggregateRoot<SprintEntity>() {
    constructor(sprint: Sprint) : this(
        id = sprint.id,
        projectId = sprint.sprintId.projectId,
        sprintId = sprint.sprintId.sprintId,
        sprintName = sprint.sprintName,
        startDate = sprint.startDate,
        endDate = sprint.endDate,
        status = sprint.status,
        sprintGoal = sprint.sprintGoal,
        sprintBacklogItems = sprint.sprintBacklogItems.map { SprintBacklogItemEntity(it) }.toMutableSet(),
    ) {
        sprint.sprintBacklogItems.forEach {
            it.productBacklogItemCommittedEvents.forEach { event ->
                this.registerEvent(event)
            }
            it.sprintBacklogItemMarkedAsDoneEvents.forEach { event ->
                this.registerEvent(event)
            }
            it.sprintBacklogItemUnmarkedAsDoneEvents.forEach { event ->
                this.registerEvent(event)
            }
            it.sprintBacklogItemUncommitedFromSprintEvents.forEach { event ->
                this.registerEvent(event)
            }
        }
        sprint.sprintCanceledEvents.forEach { this.registerEvent(it) }
    }

    fun toSprint(): Sprint =
        Sprint(
            id = id,
            sprintId = SprintId(projectId = projectId, sprintId = sprintId),
            sprintName = sprintName,
            startDate = startDate,
            endDate = endDate,
            status = status,
            sprintGoal = sprintGoal,
            sprintBacklogItems = this.sprintBacklogItems.map { it.toSprintBacklogItem() }.toMutableSet(),
        )
}
