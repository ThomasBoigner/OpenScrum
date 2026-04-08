package at.fhtw.openscrum.management.application

import at.fhtw.openscrum.management.application.command.CreateProjectCommand
import at.fhtw.openscrum.management.application.dtos.ProjectDto
import at.fhtw.openscrum.management.domain.model.project.Project
import at.fhtw.openscrum.management.domain.model.project.ProjectRepository
import at.fhtw.openscrum.management.domain.model.project.ProjectService
import at.fhtw.openscrum.management.domain.model.user.UserId
import at.fhtw.openscrum.management.domain.model.user.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ProjectApplicationService(
    private val projectService: ProjectService,
    private val projectRepository: ProjectRepository,
    private val userRepository: UserRepository,
    private val log: Logger = LoggerFactory.getLogger(ProjectApplicationService::class.java),
) {
    fun getProjects(): List<ProjectDto> {
        log.info("Trying to get all projects")
        val projects = projectRepository.findAll()
        log.info("Found all ({}) projects", projects.size)
        return projects.map { ProjectDto(it) }
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
}
