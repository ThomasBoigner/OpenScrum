package at.fhtw.openscrum.scrum.infrastructure.messaging

import at.fhtw.openscrum.management.domain.model.project.ProjectCreated
import at.fhtw.openscrum.management.domain.model.project.ProjectId
import at.fhtw.openscrum.scrum.application.ProjectApplicationService
import at.fhtw.openscrum.scrum.application.command.CreateProjectCommand
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class ManagementEventListenerTest {
    lateinit var managementEventListener: ManagementEventListener

    @Mock
    lateinit var projectApplicationService: ProjectApplicationService

    @BeforeEach
    fun setUp() {
        managementEventListener = ManagementEventListener(projectApplicationService)
    }

    @Test
    fun ensureReceiveProjectCreatedEventWorksProperly() {
        // Given
        val projectId = UUID.randomUUID()
        val projectName = "OpenScrum"

        val event = ProjectCreated(
            projectId = ProjectId(projectId),
            projectName = projectName,
        )

        // When
        managementEventListener.receiveProjectCreatedEvent(event)

        // Then
        verify(projectApplicationService).createProject(
            CreateProjectCommand(
                projectId = projectId,
                projectName = projectName,
            ),
        )
    }
}
