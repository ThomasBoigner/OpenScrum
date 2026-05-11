package at.fhtw.openscrum.scrum.infrastructure.messaging

import at.fhtw.openscrum.scrum.application.ProductBacklogItemApplicationService
import at.fhtw.openscrum.scrum.application.ProjectApplicationService
import at.fhtw.openscrum.scrum.application.SprintApplicationService
import at.fhtw.openscrum.scrum.application.command.CompleteSprintsCommand
import at.fhtw.openscrum.scrum.application.command.InitializeSprintCommand
import at.fhtw.openscrum.scrum.application.command.MarkAsCommitedToSprintCommand
import at.fhtw.openscrum.scrum.application.command.MarkAsDoneCommand
import at.fhtw.openscrum.scrum.application.command.ScheduleSprintCommand
import at.fhtw.openscrum.scrum.application.command.UncommitFromSprintCommand
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemId
import at.fhtw.openscrum.scrum.domain.model.project.ProjectId
import at.fhtw.openscrum.scrum.domain.model.project.SprintLength
import at.fhtw.openscrum.scrum.domain.model.project.SprintScheduled
import at.fhtw.openscrum.scrum.domain.model.sprint.ProductBacklogItemCommitted
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintBacklogItemMarkedAsDone
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintBacklogItemUncommitedFromSprint
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintBacklogItemUnmarkedAsDone
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintCanceled
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintCompleted
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.springframework.modulith.moments.WeekHasPassed
import java.time.LocalDate
import java.time.Year
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class ScrumEventListenerTest {
    lateinit var scrumEventListener: ScrumEventListener

    @Mock
    lateinit var projectApplicationService: ProjectApplicationService

    @Mock
    lateinit var sprintApplicationService: SprintApplicationService

    @Mock
    lateinit var productBacklogItemApplicationService: ProductBacklogItemApplicationService

    @BeforeEach
    fun setUp() {
        scrumEventListener =
            ScrumEventListener(
                projectApplicationService,
                sprintApplicationService,
                productBacklogItemApplicationService,
            )
    }

    @Test
    fun ensureReceiveSprintScheduledEventWorksProperly() {
        // Given
        val event =
            SprintScheduled(
                projectId = ProjectId(UUID.randomUUID()),
                sprintLength = SprintLength(2),
            )

        // When
        scrumEventListener.receiveSprintScheduledEvent(event)

        // Then
        verify(sprintApplicationService).initializeSprint(
            InitializeSprintCommand(
                projectId = event.projectId.token,
                sprintLength = event.sprintLength.length,
            ),
        )
    }

    @Test
    fun ensureReceiveProductBacklogItemCommitedEventWorksProperly() {
        // Given
        val productBacklogItemId =
            ProductBacklogItemId(projectId = UUID.randomUUID(), productBacklogItemId = UUID.randomUUID())
        val event = ProductBacklogItemCommitted(productBacklogItemId = productBacklogItemId)

        // When
        scrumEventListener.receiveProductBacklogItemCommitedEvent(event)

        // Then
        verify(productBacklogItemApplicationService).markAsCommittedToSprint(
            MarkAsCommitedToSprintCommand(
                projectId = productBacklogItemId.projectId,
                productBacklogItemId = productBacklogItemId.productBacklogItemId,
            ),
        )
    }

    @Test
    fun ensureReceiveSprintBacklogItemMarkedAsDoneEventWorksProperly() {
        // Given
        val productBacklogItemId =
            ProductBacklogItemId(projectId = UUID.randomUUID(), productBacklogItemId = UUID.randomUUID())
        val event = SprintBacklogItemMarkedAsDone(productBacklogItemId = productBacklogItemId)

        // When
        scrumEventListener.receiveSprintBacklogItemMarkedAsDoneEvent(event)

        // Then
        verify(productBacklogItemApplicationService).markAsCommitedToSprintDone(
            MarkAsDoneCommand(
                projectId = productBacklogItemId.projectId,
                productBacklogItemId = productBacklogItemId.productBacklogItemId,
            ),
        )
    }

    @Test
    fun ensureReceiveSprintBacklogItemUnmarkedAsDoneEventWorksProperly() {
        // Given
        val productBacklogItemId =
            ProductBacklogItemId(projectId = UUID.randomUUID(), productBacklogItemId = UUID.randomUUID())
        val event = SprintBacklogItemUnmarkedAsDone(productBacklogItemId = productBacklogItemId)

        // When
        scrumEventListener.receiveSprintBacklogItemUnmarkedAsDoneEvent(event)

        // Then
        verify(productBacklogItemApplicationService).markAsCommittedToSprint(
            MarkAsCommitedToSprintCommand(
                projectId = productBacklogItemId.projectId,
                productBacklogItemId = productBacklogItemId.productBacklogItemId,
            ),
        )
    }

    @Test
    fun ensureReceiveSprintBacklogItemUncommitedFromSprintEventWorksProperly() {
        // Given
        val productBacklogItemId =
            ProductBacklogItemId(projectId = UUID.randomUUID(), productBacklogItemId = UUID.randomUUID())
        val event = SprintBacklogItemUncommitedFromSprint(productBacklogItemId = productBacklogItemId)

        // When
        scrumEventListener.receiveSprintBacklogItemUncommitedFromSprintEvent(event)

        // Then
        verify(productBacklogItemApplicationService).uncommitFromSprint(
            UncommitFromSprintCommand(
                projectId = productBacklogItemId.projectId,
                productBacklogItemId = productBacklogItemId.productBacklogItemId,
            ),
        )
    }

    @Test
    fun ensureReceiveSprintCanceledEventWorksProperly() {
        // Given
        val projectId = UUID.randomUUID()
        val sprintId = UUID.randomUUID()
        val event = SprintCanceled(SprintId(projectId = projectId, sprintId = sprintId))

        // When
        scrumEventListener.receiveSprintCanceledEvent(event)

        // Then
        verify(projectApplicationService).scheduleSprint(
            ScheduleSprintCommand(
                projectId = projectId,
            ),
        )
    }

    @Test
    fun ensureReceiveWeekHasPassedEventWorksProperly() {
        // Given
        val weekHasPassed = WeekHasPassed.of(Year.of(2025), 1)

        // When
        scrumEventListener.receiveWeekHasPassed(weekHasPassed)

        // Then
        verify(sprintApplicationService).completeSprints(
            CompleteSprintsCommand(LocalDate.of(2025, 1, 6)),
        )
    }

    @Test
    fun ensureReceiveSprintCompletedEventWorksProperly() {
        // Given
        val projectId = UUID.randomUUID()
        val sprintId = UUID.randomUUID()
        val event = SprintCompleted(SprintId(projectId = projectId, sprintId = sprintId))

        // When
        scrumEventListener.receiveSprintCompletedEvent(event)

        // Then
        verify(projectApplicationService).scheduleSprint(
            ScheduleSprintCommand(
                projectId = projectId,
            ),
        )
    }
}
