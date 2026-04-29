package at.fhtw.openscrum.scrum.infrastructure.messaging

import at.fhtw.openscrum.scrum.application.SprintApplicationService
import at.fhtw.openscrum.scrum.application.command.InitializeSprintCommand
import at.fhtw.openscrum.scrum.domain.model.project.ProjectId
import at.fhtw.openscrum.scrum.domain.model.project.SprintLength
import at.fhtw.openscrum.scrum.domain.model.project.SprintScheduled
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

    @BeforeEach
    fun setUp() {
        scrumEventListener = ScrumEventListener(sprintApplicationService)
    }

    @Test
    fun ensureReceiveSprintScheduledEventWorksProperly() {
        // Given
        val event = SprintScheduled(
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
}
