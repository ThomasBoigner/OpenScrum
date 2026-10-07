package at.fhtw.openscrum.management.application

import at.fhtw.openscrum.management.application.command.CancelProjectCommand
import at.fhtw.openscrum.management.application.command.CreateProjectCommand
import at.fhtw.openscrum.management.application.command.UpdateProjectCommand
import at.fhtw.openscrum.management.application.dtos.ProjectDto
import at.fhtw.openscrum.management.application.mappers.ProjectMapper
import at.fhtw.openscrum.management.domain.model.project.ProjectId
import at.fhtw.openscrum.management.domain.model.project.ProjectRepository
import at.fhtw.openscrum.management.domain.model.project.ProjectService
import at.fhtw.openscrum.management.domain.model.user.UserId
import at.fhtw.openscrum.management.domain.model.user.UserRepository
import at.fhtw.openscrum.management.domain.util.Page
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service("managementProjectApplicationService")
@Transactional(readOnly = true)
class ProjectApplicationService(
    private val projectMapper: ProjectMapper,
    private val projectService: ProjectService,
    private val projectRepository: ProjectRepository,
    private val userRepository: UserRepository,
    private val log: Logger = LoggerFactory.getLogger(ProjectApplicationService::class.java),
) {
    fun getProject(projectId: UUID): ProjectDto? {
        log.debug("Trying to get project with id {}", projectId)
        val project =
            projectRepository.findByProjectId(ProjectId(projectId))
                ?: return null
        log.info("Found project {}", project)
        return ProjectDto(project)
    }

    fun getProjects(
        authenticatedUserUsername: String,
        nameQuery: String? = null,
        page: Int = 0,
        size: Int = 5,
    ): Page<ProjectDto> {
        log.info(
            "User {} is trying to find page {} with size {} of his projects matching query '{}'",
            authenticatedUserUsername,
            page,
            size,
            nameQuery,
        )
        val authenticatedUser =
            userRepository.findByUsername(authenticatedUserUsername)
                ?: throw IllegalArgumentException("Could not find user with username $authenticatedUserUsername")
        val projectPage = projectService.getProjects(authenticatedUser, nameQuery, page, size)
        log.info("Found {} of {} projects of user {}", projectPage.numberOfElements, projectPage.totalElements, authenticatedUser)
        return projectMapper.toProjectDtoPage(projectPage)
    }

    @Transactional(readOnly = false)
    fun createProject(
        authenticatedUserUsername: String,
        command: CreateProjectCommand,
    ): ProjectDto {
        log.debug("User {} is trying to create project with command: {}", authenticatedUserUsername, command)

        val authenticatedUser =
            userRepository.findByUsername(authenticatedUserUsername)
                ?: throw IllegalArgumentException("Could not find user with username $authenticatedUserUsername")

        val productOwner = userRepository.findByUserId(UserId(command.productOwnerId))
        val scrumMaster = userRepository.findByUserId(UserId(command.scrumMasterId))
        val developers =
            command.developerIds
                .mapNotNull { developerId -> userRepository.findByUserId(UserId(developerId)) }
                .toSet()

        return ProjectDto(
            projectService.createProject(
                authenticatedUser = authenticatedUser,
                projectName = command.projectName,
                productOwner = productOwner,
                scrumMaster = scrumMaster,
                developers = developers,
            ),
        )
    }

    @Transactional(readOnly = false)
    fun updateProject(
        authenticatedUserUsername: String,
        command: UpdateProjectCommand,
    ): ProjectDto {
        log.debug("User {} is trying to update project with command: {}", authenticatedUserUsername, command)

        val authenticatedUser =
            userRepository.findByUsername(authenticatedUserUsername)
                ?: throw IllegalArgumentException("Could not find user with username $authenticatedUserUsername")

        val productOwner = userRepository.findByUserId(UserId(command.productOwnerId))
        val scrumMaster = userRepository.findByUserId(UserId(command.scrumMasterId))
        val developers =
            command.developerIds
                .mapNotNull { developerId -> userRepository.findByUserId(UserId(developerId)) }
                .toSet()

        return ProjectDto(
            projectService.updateProject(
                authenticatedUser = authenticatedUser,
                projectId = ProjectId(command.projectId),
                projectName = command.projectName,
                productOwner = productOwner,
                scrumMaster = scrumMaster,
                developers = developers,
            ),
        )
    }

    @Transactional(readOnly = false)
    fun cancelProject(
        authenticatedUserUsername: String,
        command: CancelProjectCommand,
    ) {
        log.debug("User {} is trying to cancel project with id {}", authenticatedUserUsername, command.projectId)

        val authenticatedUser =
            userRepository.findByUsername(authenticatedUserUsername)
                ?: throw IllegalArgumentException("Could not find user with username $authenticatedUserUsername")

        projectService.cancelProject(
            authenticatedUser = authenticatedUser,
            projectId = ProjectId(command.projectId),
        )
    }
}
