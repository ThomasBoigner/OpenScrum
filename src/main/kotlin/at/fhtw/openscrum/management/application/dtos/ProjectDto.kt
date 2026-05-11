package at.fhtw.openscrum.management.application.dtos

import at.fhtw.openscrum.management.domain.model.project.Project
import at.fhtw.openscrum.management.domain.model.user.UserId
import java.util.UUID

data class ProjectDto(
    val projectId: UUID,
    val projectName: String,
    val productOwnerId: UUID,
    val scrumMasterId: UUID,
    val developerIds: Set<UUID>,
) {
    constructor(project: Project) : this(
        projectId = project.projectId.token,
        projectName = project.projectName,
        productOwnerId = project.productOwnerId.token,
        scrumMasterId = project.scrumMasterId.token,
        developerIds = project.developerIds.map { it.token }.toSet(),
    )
}
