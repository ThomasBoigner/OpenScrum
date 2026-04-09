package at.fhtw.openscrum.management.infrastructure.persistence.jpa.project

import at.fhtw.openscrum.management.domain.model.project.Project
import at.fhtw.openscrum.management.domain.model.project.ProjectId
import at.fhtw.openscrum.management.domain.model.user.UserId
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.util.UUID

@Entity
class ProjectEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val projectId: UUID,
    val projectName: String,
    val productOwnerId: UUID,
    val scrumMasterId: UUID,
    val developerIds: Set<UUID> = setOf(),
) {
    constructor(project: Project) : this(
        id = project.id,
        projectId = project.projectId.token,
        projectName = project.projectName,
        productOwnerId = project.productOwnerId.token,
        scrumMasterId = project.scrumMasterId.token,
        developerIds = project.developerIds.map { it.token }.toSet(),
    )

    constructor() : this(
        id = null,
        projectId = UUID.randomUUID(),
        projectName = "",
        productOwnerId = UUID.randomUUID(),
        scrumMasterId = UUID.randomUUID(),
        developerIds = setOf(),
    )

    fun toProject(): Project =
        Project(
            id = id,
            projectId = ProjectId(projectId),
            projectName = projectName,
            productOwnerId = UserId(productOwnerId),
            scrumMasterId = UserId(scrumMasterId),
            developerIds = developerIds.map { UserId(it) }.toSet(),
        )
}
