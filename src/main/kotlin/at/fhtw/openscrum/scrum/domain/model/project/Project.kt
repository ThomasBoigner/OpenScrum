package at.fhtw.openscrum.scrum.domain.model.project

import at.fhtw.openscrum.scrum.domain.model.teammember.ProductOwner
import at.fhtw.openscrum.scrum.domain.model.teammember.ScrumMaster

class Project(
    val id: Long? = null,
    val projectId: ProjectId,
    val projectName: String,
    sprintLength: SprintLength = SprintLength(2),
    productGoal: String? = null,
    definitionOfDone: String? = null,
    val sprintScheduledEvents: MutableList<SprintScheduled> = mutableListOf(),
) {
    var sprintLength: SprintLength = sprintLength
        private set
    var productGoal: String? = productGoal
        private set
    var definitionOfDone: String? = definitionOfDone
        private set

    init {
        sprintScheduledEvents.add(SprintScheduled(projectId = projectId, sprintLength = sprintLength))
    }

    fun defineSprintLength(
        scrumMaster: ScrumMaster?,
        sprintLength: Long,
    ) {
        require(scrumMaster?.teamMemberId?.projectId == this.projectId.token) { "You are not the scrum master of this project!" }
        this.sprintLength = SprintLength(sprintLength)
    }

    fun defineProductGoal(
        productOwner: ProductOwner?,
        productGoal: String?,
    ) {
        require(productOwner?.teamMemberId?.projectId == this.projectId.token) { "You are not the product owner of this project!" }
        this.productGoal = if (!productGoal.isNullOrBlank()) productGoal else null
    }

    fun defineDefinitionOfDone(
        scrumMaster: ScrumMaster?,
        definitionOfDone: String?,
    ) {
        require(scrumMaster?.teamMemberId?.projectId == this.projectId.token) { "You are not the scrum master of this project!" }
        this.definitionOfDone = if (!definitionOfDone.isNullOrBlank()) definitionOfDone else null
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
