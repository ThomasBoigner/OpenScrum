package at.fhtw.openscrum.management.infrastructure.persistence.jpa.project

import at.fhtw.openscrum.management.domain.model.project.Project
import at.fhtw.openscrum.management.domain.model.project.ProjectId
import at.fhtw.openscrum.management.domain.model.user.UserId
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.springframework.data.domain.AbstractAggregateRoot
import java.util.UUID

@Entity(name = "managementProjectEntity")
class ProjectEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var projectId: UUID,
    var projectName: String,
    var productOwnerId: UUID,
    var scrumMasterId: UUID,
    var developerIds: Set<UUID> = setOf(),
) : AbstractAggregateRoot<ProjectEntity>() {
    constructor(project: Project) : this(
        id = project.id,
        projectId = project.projectId.token,
        projectName = project.projectName,
        productOwnerId = project.productOwnerId.token,
        scrumMasterId = project.scrumMasterId.token,
        developerIds = project.developerIds.map { it.token }.toSet(),
    ) {
        project.projectCreatedEvents.forEach { this.registerEvent(it) }
        project.scrumMasterAssignedEvents.forEach { this.registerEvent(it) }
        project.productOwnerAssignedEvents.forEach { this.registerEvent(it) }
        project.developerAssignedEvents.forEach { this.registerEvent(it) }
    }

    fun toProject(): Project =
        Project(
            id = id,
            projectId = ProjectId(projectId),
            projectName = projectName,
            productOwnerId = UserId(productOwnerId),
            scrumMasterId = UserId(scrumMasterId),
            developerIds = developerIds.map { UserId(it) }.toSet(),
            projectCreatedEvents = mutableListOf(),
        )
}
