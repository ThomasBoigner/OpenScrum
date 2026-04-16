package at.fhtw.openscrum.scrum.infrastructure.messaging

import at.fhtw.openscrum.management.domain.model.project.ProjectCreated
import at.fhtw.openscrum.scrum.application.ProjectApplicationService
import at.fhtw.openscrum.scrum.application.command.CreateProjectCommand
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.modulith.events.ApplicationModuleListener
import org.springframework.stereotype.Component

@Component
class ManagementEventListener(
    private val projectApplicationService: ProjectApplicationService,
    private val log: Logger = LoggerFactory.getLogger(ManagementEventListener::class.java),
) {
    @ApplicationModuleListener
    fun receiveProjectCreatedEvent(event: ProjectCreated) {
        log.trace("Received create project event: {}", event)
        projectApplicationService.createProject(
            CreateProjectCommand(
                projectId = event.projectId.token,
                projectName = event.projectName,
            ),
        )
    }
}
