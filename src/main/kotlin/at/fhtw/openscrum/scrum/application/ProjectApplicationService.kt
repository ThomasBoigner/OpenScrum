package at.fhtw.openscrum.scrum.application

import at.fhtw.openscrum.scrum.application.command.CreateProjectCommand
import at.fhtw.openscrum.scrum.application.command.DefineDefinitionOfDoneCommand
import at.fhtw.openscrum.scrum.application.command.DefineProductGoalCommand
import at.fhtw.openscrum.scrum.application.command.DefineSprintLengthCommand
import at.fhtw.openscrum.scrum.application.dtos.ProjectDto
import at.fhtw.openscrum.scrum.domain.model.project.Project
import at.fhtw.openscrum.scrum.domain.model.project.ProjectId
import at.fhtw.openscrum.scrum.domain.model.project.ProjectRepository
import at.fhtw.openscrum.scrum.domain.model.teammember.ProductOwnerRepository
import at.fhtw.openscrum.scrum.domain.model.teammember.ScrumMasterRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service("scrumProjectApplicationService")
@Transactional(readOnly = true)
class ProjectApplicationService(
    private val projectRepository: ProjectRepository,
    private val scrumMasterRepository: ScrumMasterRepository,
    private val productOwnerRepository: ProductOwnerRepository,
    private val log: Logger = LoggerFactory.getLogger(ProjectApplicationService::class.java),
) {
    fun getProject(projectId: UUID): ProjectDto? {
        log.debug("Trying to get project with id {}", projectId)
        val project = projectRepository.findByProjectId(ProjectId(projectId))
        log.info(project?.let { "Found project $it" } ?: "Project with project id $projectId could not be found")
        return project?.let { ProjectDto(it) }
    }

    @Transactional(readOnly = false)
    fun createProject(command: CreateProjectCommand): ProjectDto {
        log.debug("Trying to create project with command: {}", command)

        val project =
            Project(
                projectId = ProjectId(command.projectId),
                projectName = command.projectName,
            )

        log.info("Created project {}", project)
        return ProjectDto(projectRepository.save(project))
    }

    @Transactional(readOnly = false)
    fun defineSprintLength(
        authenticatedUserUsername: String,
        command: DefineSprintLengthCommand,
    ): ProjectDto {
        log.debug("Trying to define sprint length of project with command {}", command)

        val project =
            projectRepository.findByProjectId(ProjectId(command.projectId)) ?: throw IllegalArgumentException(
                "Could not find project with id ${command.projectId}",
            )
        val scrumMaster = scrumMasterRepository.findByProjectIdAndUsername(command.projectId, authenticatedUserUsername)

        project.defineSprintLength(scrumMaster, command.sprintLength)
        log.info("Updated sprint length of project {}", project)
        return ProjectDto(projectRepository.save(project))
    }

    @Transactional(readOnly = false)
    fun defineProductGoal(
        authenticatedUserUsername: String,
        command: DefineProductGoalCommand,
    ): ProjectDto {
        log.debug("Trying to define product goal of project with command {}", command)
        val project =
            projectRepository.findByProjectId(ProjectId(command.projectId)) ?: throw IllegalArgumentException(
                "Could not find project with id ${command.projectId}",
            )
        val productOwner = productOwnerRepository.findByProjectIdAndUsername(command.projectId, authenticatedUserUsername)

        project.defineProductGoal(productOwner, command.productGoal)
        log.info("Updated product goal of project {}", project)
        return ProjectDto(projectRepository.save(project))
    }

    @Transactional(readOnly = false)
    fun defineDefinitionOfDone(
        authenticatedUserUsername: String,
        command: DefineDefinitionOfDoneCommand,
    ): ProjectDto {
        log.debug("Trying to define definition of done of project with command {}", command)
        val project =
            projectRepository.findByProjectId(ProjectId(command.projectId)) ?: throw IllegalArgumentException(
                "Could not find project with id ${command.projectId}",
            )
        val scrumMaster = scrumMasterRepository.findByProjectIdAndUsername(command.projectId, authenticatedUserUsername)

        project.defineDefinitionOfDone(scrumMaster, command.definitionOfDone)
        log.info("Updated definition of done of project {}", project)
        return ProjectDto(projectRepository.save(project))
    }
}
