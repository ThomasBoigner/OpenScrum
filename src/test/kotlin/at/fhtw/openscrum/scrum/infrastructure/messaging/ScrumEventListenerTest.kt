package at.fhtw.openscrum.scrum.infrastructure.messaging

import at.fhtw.openscrum.scrum.application.ProductBacklogItemApplicationService
import at.fhtw.openscrum.scrum.application.SprintApplicationService
import at.fhtw.openscrum.scrum.application.command.InitializeSprintCommand
import at.fhtw.openscrum.scrum.application.command.MarkAsCommitedToSprintCommand
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemId
import at.fhtw.openscrum.scrum.domain.model.project.ProjectId
import at.fhtw.openscrum.scrum.domain.model.project.SprintLength
import at.fhtw.openscrum.scrum.domain.model.project.SprintScheduled
import at.fhtw.openscrum.scrum.domain.model.sprint.ProductBacklogItemCommited
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class ScrumEventListenerTest {
    lateinit var scrumEventListener: ScrumEventListener

    @Mock
    lateinit var sprintApplicationService: SprintApplicationService

    @Mock
    lateinit var productBacklogItemApplicationService: ProductBacklogItemApplicationService

    @BeforeEach
    fun setUp() {
        scrumEventListener = ScrumEventListener(sprintApplicationService, productBacklogItemApplicationService)
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
        val productBacklogItemId = ProductBacklogItemId(projectId = UUID.randomUUID(), productBacklogItemId = UUID.randomUUID())
        val event = ProductBacklogItemCommited(productBacklogItemId = productBacklogItemId)

        // When
        scrumEventListener.receiveProductBacklogItemCommitedEvent(event)

        // Then
        verify(productBacklogItemApplicationService).markAsCommitedToSprint(
            MarkAsCommitedToSprintCommand(
                projectId = productBacklogItemId.projectId,
                productBacklogItemId = productBacklogItemId.productBacklogItemId,
            ),
        )
    }
}
