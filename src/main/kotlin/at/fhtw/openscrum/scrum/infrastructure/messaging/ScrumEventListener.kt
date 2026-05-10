package at.fhtw.openscrum.scrum.infrastructure.messaging

import at.fhtw.openscrum.scrum.application.ProductBacklogItemApplicationService
import at.fhtw.openscrum.scrum.application.ProjectApplicationService
import at.fhtw.openscrum.scrum.application.SprintApplicationService
import at.fhtw.openscrum.scrum.application.command.InitializeSprintCommand
import at.fhtw.openscrum.scrum.application.command.MarkAsCommitedToSprintCommand
import at.fhtw.openscrum.scrum.application.command.MarkAsDoneCommand
import at.fhtw.openscrum.scrum.application.command.MarkAsInBacklogCommand
import at.fhtw.openscrum.scrum.application.command.ScheduleSprintCommand
import at.fhtw.openscrum.scrum.domain.model.project.SprintScheduled
import at.fhtw.openscrum.scrum.domain.model.sprint.ProductBacklogItemCommitted
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintBacklogItemMarkedAsDone
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintBacklogItemUncommitedFromSprint
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintBacklogItemUnmarkedAsDone
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintCanceled
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.modulith.events.ApplicationModuleListener
import org.springframework.stereotype.Component

@Component
class ScrumEventListener(
    private val projectApplicationService: ProjectApplicationService,
    private val sprintApplicationService: SprintApplicationService,
    private val productBacklogItemApplicationService: ProductBacklogItemApplicationService,
    private val log: Logger = LoggerFactory.getLogger(ScrumEventListener::class.java),
) {
    @ApplicationModuleListener
    fun receiveSprintScheduledEvent(event: SprintScheduled) {
        log.trace("Received sprint scheduledEvent event: {}", event)
        sprintApplicationService.initializeSprint(
            InitializeSprintCommand(
                projectId = event.projectId.token,
                sprintLength = event.sprintLength.length,
            ),
        )
    }


    @ApplicationModuleListener
    fun receiveProductBacklogItemCommitedEvent(event: ProductBacklogItemCommitted) {
        log.trace("Received productBacklogItemCommitedEvent event: {}", event)
        productBacklogItemApplicationService.markAsCommittedToSprint(
            MarkAsCommitedToSprintCommand(
                projectId = event.productBacklogItemId.projectId,
                productBacklogItemId = event.productBacklogItemId.productBacklogItemId,
            ),
        )
    }

    @ApplicationModuleListener
    fun receiveSprintBacklogItemMarkedAsDoneEvent(event: SprintBacklogItemMarkedAsDone) {
        log.trace("Received sprintBacklogItemMarkedAsDone event: {}", event)
        productBacklogItemApplicationService.markAsCommitedToSprintDone(
            MarkAsDoneCommand(
                projectId = event.productBacklogItemId.projectId,
                productBacklogItemId = event.productBacklogItemId.productBacklogItemId,
            ),
        )
    }

    @ApplicationModuleListener
    fun receiveSprintBacklogItemUnmarkedAsDoneEvent(event: SprintBacklogItemUnmarkedAsDone) {
        log.trace("Received sprintBacklogItemUnmarkedAsDone event: {}", event)
        productBacklogItemApplicationService.markAsCommittedToSprint(
            MarkAsCommitedToSprintCommand(
                projectId = event.productBacklogItemId.projectId,
                productBacklogItemId = event.productBacklogItemId.productBacklogItemId,
            ),
        )
    }

    @ApplicationModuleListener
    fun receiveSprintBacklogItemUncommitedFromSprintEvent(event: SprintBacklogItemUncommitedFromSprint) {
        log.trace("Received sprintBacklogUncommitedFromSprint event: {}", event)
        productBacklogItemApplicationService.markAsInBacklog(
            MarkAsInBacklogCommand(
                projectId = event.productBacklogItemId.projectId,
                productBacklogItemId = event.productBacklogItemId.productBacklogItemId,
            ),
        )
    }

    @ApplicationModuleListener
    fun receiveSprintCanceledEvent(event: SprintCanceled) {
        log.trace("Received sprintCanceled event: {}", event)
        projectApplicationService.scheduleSprint(
            ScheduleSprintCommand(
                projectId = event.sprintId.projectId,
            ),
        )
    }
}
