package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.project

import at.fhtw.openscrum.scrum.domain.model.project.Project
import at.fhtw.openscrum.scrum.domain.model.project.ProjectId
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.springframework.data.domain.AbstractAggregateRoot
import java.util.UUID

class ProjectEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val projectId: UUID,
    val projectName: String,
    val sprintLength: Int,
    val definitionOfDone: String?,
    val productGoal: String?,
) : AbstractAggregateRoot<ProjectEntity>() {
    constructor(project: Project) : this(
        id = project.id,
        projectId = project.projectId.token,
        projectName = project.projectName,
        sprintLength = project.sprintLength,
        definitionOfDone = project.definitionOfDone,
        productGoal = project.productGoal,
    )

    constructor() : this(
        id = null,
        projectId = UUID.randomUUID(),
        projectName = "",
        sprintLength = 1,
        definitionOfDone = null,
        productGoal = null,
    )

    fun toProject(): Project =
        Project(
            id = id,
            projectId = ProjectId(projectId),
            projectName = projectName,
            sprintLength = sprintLength,
            definitionOfDone = definitionOfDone,
            productGoal = productGoal,
        )
}
