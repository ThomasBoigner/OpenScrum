package at.fhtw.openscrum.scrum.application.dtos

import at.fhtw.openscrum.scrum.domain.model.project.Project
import java.util.UUID

class ProjectDto(
    val projectId: UUID,
    val projectName: String,
    val sprintLength: Long,
    val definitionOfDone: String?,
    val productGoal: String?,
) {
    constructor(project: Project) : this(
        projectId = project.projectId.token,
        projectName = project.projectName,
        sprintLength = project.sprintLength.length,
        definitionOfDone = project.definitionOfDone,
        productGoal = project.productGoal,
    )
}
