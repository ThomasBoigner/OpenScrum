package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.project

import at.fhtw.openscrum.scrum.domain.model.project.Project
import at.fhtw.openscrum.scrum.domain.model.project.ProjectId
import at.fhtw.openscrum.scrum.domain.model.project.SprintLength
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.springframework.data.domain.AbstractAggregateRoot
import java.util.UUID

@Entity(name = "scrumProjectEntity")
class ProjectEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var projectId: UUID,
    var projectName: String,
    var sprintLength: Int,
    var definitionOfDone: String?,
    var productGoal: String?,
) : AbstractAggregateRoot<ProjectEntity>() {
    constructor(project: Project) : this(
        id = project.id,
        projectId = project.projectId.token,
        projectName = project.projectName,
        sprintLength = project.sprintLength.length,
        definitionOfDone = project.definitionOfDone,
        productGoal = project.productGoal,
    )

    fun toProject(): Project =
        Project(
            id = id,
            projectId = ProjectId(projectId),
            projectName = projectName,
            sprintLength = SprintLength(sprintLength),
            definitionOfDone = definitionOfDone,
            productGoal = productGoal,
        )
}
