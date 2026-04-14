package at.fhtw.openscrum.scrum.domain.model.project

class Project(
    val id: Long? = null,
    val projectId: ProjectId,
    val projectName: String,
    val sprintLength: Int = 2,
    val definitionOfDone: String? = null,
    val productGoal: String? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Project

        return projectId == other.projectId
    }

    override fun hashCode(): Int = projectId.hashCode()

    override fun toString(): String =
        "Project(projectId=$projectId, projectName='$projectName', sprintLength=$sprintLength, definitionOfDone=$definitionOfDone, productGoal=$productGoal)"
}
