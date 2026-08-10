package at.fhtw.openscrum.management.domain.model.project

import at.fhtw.openscrum.management.domain.model.user.User
import at.fhtw.openscrum.management.domain.model.user.UserId

class Project(
    val id: Long? = null,
    val projectId: ProjectId = ProjectId(),
    projectName: String,
    productOwnerId: UserId,
    scrumMasterId: UserId,
    developerIds: Set<UserId> = setOf(),
    val projectCreatedEvents: MutableList<ProjectCreated> = mutableListOf(ProjectCreated(projectId, projectName)),
    val projectInformationChangedEvents: MutableList<ProjectInformationChanged> = mutableListOf(),
    val scrumMasterAssignedEvents: MutableList<ScrumMasterAssigned> = mutableListOf(),
    val productOwnerAssignedEvents: MutableList<ProductOwnerAssigned> = mutableListOf(),
    val developerAssignedEvents: MutableList<DeveloperAssigned> = mutableListOf(),
    val scrumMasterUnassignedEvents: MutableList<ScrumMasterUnassigned> = mutableListOf(),
    val productOwnerUnassignedEvents: MutableList<ProductOwnerUnassigned> = mutableListOf(),
    val developerUnassignedEvents: MutableList<DeveloperUnassigned> = mutableListOf(),
    val projectCanceledEvents: MutableList<ProjectCanceled> = mutableListOf(),
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

    var developerIds: Set<UserId> = developerIds
        private set

    init {
        this.projectName = projectName
    }

    fun update(
        projectName: String,
        productOwner: User,
        scrumMaster: User,
        developers: Set<User>,
    ) {
        if (this.projectName != projectName) {
            this.projectName = projectName
            projectInformationChangedEvents.add(ProjectInformationChanged(projectId, projectName))
        }

        if (productOwner.userId != productOwnerId) {
            productOwnerUnassignedEvents.add(ProductOwnerUnassigned(productOwnerId, projectId))
            productOwnerAssignedEvents.add(
                ProductOwnerAssigned(productOwner.userId, projectId, productOwner.username, productOwner.fullName),
            )
            productOwnerId = productOwner.userId
        }

        if (scrumMaster.userId != scrumMasterId) {
            scrumMasterUnassignedEvents.add(ScrumMasterUnassigned(scrumMasterId, projectId))
            scrumMasterAssignedEvents.add(
                ScrumMasterAssigned(scrumMaster.userId, projectId, scrumMaster.username, scrumMaster.fullName),
            )
            scrumMasterId = scrumMaster.userId
        }

        val newDeveloperIds = developers.map { it.userId }.toSet()
        developerIds
            .filter { it !in newDeveloperIds }
            .forEach { developerUnassignedEvents.add(DeveloperUnassigned(it, projectId)) }
        developers
            .filter { it.userId !in developerIds }
            .forEach { developerAssignedEvents.add(DeveloperAssigned(it.userId, projectId, it.username, it.fullName)) }
        developerIds = newDeveloperIds
    }

    fun cancel() {
        projectCanceledEvents.add(ProjectCanceled(projectId))
        productOwnerUnassignedEvents.add(ProductOwnerUnassigned(productOwnerId, projectId))
        scrumMasterUnassignedEvents.add(ScrumMasterUnassigned(scrumMasterId, projectId))
        developerIds.forEach { developerUnassignedEvents.add(DeveloperUnassigned(it, projectId)) }
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
