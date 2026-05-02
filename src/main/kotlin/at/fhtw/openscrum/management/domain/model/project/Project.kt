package at.fhtw.openscrum.management.domain.model.project

import at.fhtw.openscrum.management.domain.model.user.UserId

class Project(
    val id: Long? = null,
    val projectId: ProjectId = ProjectId(),
    projectName: String,
    productOwnerId: UserId,
    scrumMasterId: UserId,
    val developerIds: Set<UserId> = setOf(),
    val projectCreatedEvents: MutableList<ProjectCreated> = mutableListOf(ProjectCreated(projectId, projectName)),
    val scrumMasterAssignedEvents: MutableList<ScrumMasterAssigned> = mutableListOf(),
    val productOwnerAssignedEvents: MutableList<ProductOwnerAssigned> = mutableListOf(),
    val developerAssignedEvents: MutableList<DeveloperAssigned> = mutableListOf(),
) {
    var projectName: String = ""
        private set(value) {
            require(value.isNotBlank()) { "Project name must not be blank!" }
            field = value
        }

    var productOwnerId: UserId = productOwnerId
        private set

    var scrumMasterId: UserId = scrumMasterId
        private set

    init {
        this.projectName = projectName
    }

    override fun toString(): String =
        "Project(projectId=$projectId, developerIds=$developerIds, projectName='$projectName', productOwnerId=$productOwnerId, scrumMasterId=$scrumMasterId)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Project

        return projectId == other.projectId
    }

    override fun hashCode(): Int = projectId.hashCode()
}
