package at.fhtw.openscrum.management.domain.model.project

import at.fhtw.openscrum.management.domain.model.user.Role
import at.fhtw.openscrum.management.domain.model.user.User
import at.fhtw.openscrum.management.domain.model.user.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ProjectService(
    private val projectRepository: ProjectRepository,
    private val log: Logger = LoggerFactory.getLogger(UserService::class.java),
) {
    fun createProject(
        authenticatedUser: User,
        projectName: String,
        productOwner: User?,
        scrumMaster: User?,
        developers: Set<User?>,
    ): Project {
        require(authenticatedUser.role == Role.MANAGER) { "Only managers can create projects!" }
        require(productOwner != null) { "Product owner must not be null!" }
        require(scrumMaster != null) { "Scrum master must not be null!" }
        require(!projectRepository.existsByProjectName(projectName)) { "Project name is already taken!" }

        log.debug("Creating project '$projectName'")

        val project = Project(
            projectName = projectName,
            productOwnerId = productOwner.userId,
            scrumMasterId = scrumMaster.userId,
            developerIds = developers.filterNotNull().map { it.userId }.toSet(),
        )

        return projectRepository.save(project)
    }
}
