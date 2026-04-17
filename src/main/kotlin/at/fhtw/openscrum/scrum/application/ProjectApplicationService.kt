package at.fhtw.openscrum.scrum.application

import at.fhtw.openscrum.scrum.application.command.CreateProjectCommand
import at.fhtw.openscrum.scrum.application.dtos.ProjectDto
import at.fhtw.openscrum.scrum.domain.model.project.Project
import at.fhtw.openscrum.scrum.domain.model.project.ProjectId
import at.fhtw.openscrum.scrum.domain.model.project.ProjectRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service("scrumProjectApplicationService")
@Transactional(readOnly = true)
class ProjectApplicationService(
    private val projectRepository: ProjectRepository,
    private val log: Logger = LoggerFactory.getLogger(ProjectApplicationService::class.java),
) {
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
}
