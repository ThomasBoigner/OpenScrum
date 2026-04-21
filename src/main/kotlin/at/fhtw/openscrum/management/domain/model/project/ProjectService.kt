package at.fhtw.openscrum.management.domain.model.project

import at.fhtw.openscrum.management.domain.model.user.User
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ProjectService(
    private val projectRepository: ProjectRepository,
    private val log: Logger = LoggerFactory.getLogger(ProjectService::class.java),
) {
    fun createProject(
        authenticatedUser: User,
        projectName: String,
        productOwner: User?,
        scrumMaster: User?,
        developers: Set<User>,
    ): Project {
        log.debug("Trying to create project {}", projectName)
        require(authenticatedUser.role.isManager) { "Management permissions are needed!" }
        require(productOwner != null) { "Product owner does not exist!" }
        require(scrumMaster != null) { "Scrum master does not exist!" }
        require(!projectRepository.existsByProjectName(projectName)) { "Project with name $projectName already exists!" }

        val developerIds = developers.map { it.userId }.toSet()
        require(
            productOwner.userId !in developerIds &&
                scrumMaster.userId !in developerIds &&
                productOwner.userId != scrumMaster.userId,
        ) { "A user cannot have multiple roles in the same project!" }

        val projectId = ProjectId()
        val project =
            Project(
                projectId = projectId,
                projectName = projectName,
                productOwnerId = productOwner.userId,
                scrumMasterId = scrumMaster.userId,
                developerIds = developerIds,
                scrumMasterAssignedEvents =
                    mutableListOf(
                        ScrumMasterAssigned(
                            scrumMaster.userId,
                            projectId,
                            scrumMaster.username,
                            scrumMaster.fullName,
                        ),
                    ),
                productOwnerAssignedEvents =
                    mutableListOf(
                        ProductOwnerAssigned(
                            productOwner.userId,
                            projectId,
                            productOwner.username,
                            productOwner.fullName,
                        ),
                    ),
                developerAssignedEvents =
                    developers
                        .map { DeveloperAssigned(it.userId, projectId, it.username, it.fullName) }
                        .toMutableList(),
            )

        log.info("Created Project {}", project)
        return projectRepository.save(project)
    }
}
