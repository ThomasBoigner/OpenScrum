package at.fhtw.openscrum.scrum.infrastructure.messaging

import at.fhtw.openscrum.management.domain.model.project.DeveloperAssigned
import at.fhtw.openscrum.management.domain.model.project.ProductOwnerAssigned
import at.fhtw.openscrum.management.domain.model.project.ProjectCreated
import at.fhtw.openscrum.management.domain.model.project.ScrumMasterAssigned
import at.fhtw.openscrum.scrum.application.ProjectApplicationService
import at.fhtw.openscrum.scrum.application.TeamMemberApplicationService
import at.fhtw.openscrum.scrum.application.command.AssignDeveloperCommand
import at.fhtw.openscrum.scrum.application.command.AssignProductOwnerCommand
import at.fhtw.openscrum.scrum.application.command.AssignScrumMasterCommand
import at.fhtw.openscrum.scrum.application.command.CreateProjectCommand
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.modulith.events.ApplicationModuleListener
import org.springframework.stereotype.Component

@Component
class ManagementEventListener(
    private val projectApplicationService: ProjectApplicationService,
    private val teamMemberApplicationService: TeamMemberApplicationService,
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

    @ApplicationModuleListener
    fun receiveDeveloperAssignedEvent(event: DeveloperAssigned) {
        log.trace("Received developer assigned event: {}", event)
        teamMemberApplicationService.assignDeveloper(
            AssignDeveloperCommand(
                userId = event.userId.token,
                projectId = event.projectId.token,
                username = event.username,
                firstName = event.fullName.firstName,
                lastName = event.fullName.lastName,
            ),
        )
    }

    @ApplicationModuleListener
    fun receiveScrumMasterAssignedEvent(event: ScrumMasterAssigned) {
        log.trace("Received scrum master assigned event: {}", event)
        teamMemberApplicationService.assignScrumMaster(
            AssignScrumMasterCommand(
                userId = event.userId.token,
                projectId = event.projectId.token,
                username = event.username,
                firstName = event.fullName.firstName,
                lastName = event.fullName.lastName,
            ),
        )
    }

    @ApplicationModuleListener
    fun receiveProductOwnerAssignedEvent(event: ProductOwnerAssigned) {
        log.trace("Received product owner assigned event: {}", event)
        teamMemberApplicationService.assignProductOwner(
            AssignProductOwnerCommand(
                userId = event.userId.token,
                projectId = event.projectId.token,
                username = event.username,
                firstName = event.fullName.firstName,
                lastName = event.fullName.lastName,
            ),
        )
    }
}
