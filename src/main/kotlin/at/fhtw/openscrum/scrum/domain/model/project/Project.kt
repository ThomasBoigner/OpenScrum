package at.fhtw.openscrum.scrum.domain.model.project

import at.fhtw.openscrum.scrum.domain.model.teammember.ProductOwner
import at.fhtw.openscrum.scrum.domain.model.teammember.ScrumMaster

class Project(
    val id: Long? = null,
    val projectId: ProjectId,
    val projectName: String,
    val sprintLength: SprintLength = SprintLength(2),
    val productGoal: String? = null,
    definitionOfDone: DefinitionOfDone? = null,
) {
    var definitionOfDone: DefinitionOfDone? = definitionOfDone
        private set

    fun defineDefinitionOfDone(
        scrumMaster: ScrumMaster,
        definitionOfDone: String?,
    ) {
        require(scrumMaster.teamMemberId.projectId == this.projectId.token) { "You are not the scrum master of this project!" }
        this.definitionOfDone = if (!definitionOfDone.isNullOrBlank()) DefinitionOfDone(definitionOfDone) else null
    }

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
