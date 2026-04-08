package at.fhtw.openscrum.management.application.dtos

import at.fhtw.openscrum.management.domain.model.project.Project
import java.util.UUID

data class ProjectDto(
    val projectId: UUID,
    val projectName: String,
    val numberOfDevelopers: Int,
) {
    constructor(project: Project) : this(
        projectId = project.projectId.token,
        projectName = project.projectName,
        numberOfDevelopers = project.developerIds.size,
    )
}
