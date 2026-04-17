package at.fhtw.openscrum.scrum.infrastructure.messaging

import at.fhtw.openscrum.management.domain.model.project.DeveloperAssigned
import at.fhtw.openscrum.management.domain.model.project.ProductOwnerAssigned
import at.fhtw.openscrum.management.domain.model.project.ProjectCreated
import at.fhtw.openscrum.management.domain.model.project.ProjectId
import at.fhtw.openscrum.management.domain.model.project.ScrumMasterAssigned
import at.fhtw.openscrum.management.domain.model.user.FullName
import at.fhtw.openscrum.management.domain.model.user.UserId
import at.fhtw.openscrum.scrum.application.ProjectApplicationService
import at.fhtw.openscrum.scrum.application.TeamMemberApplicationService
import at.fhtw.openscrum.scrum.application.command.AssignDeveloperCommand
import at.fhtw.openscrum.scrum.application.command.AssignProductOwnerCommand
import at.fhtw.openscrum.scrum.application.command.AssignScrumMasterCommand
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

    @Mock
    lateinit var teamMemberApplicationService: TeamMemberApplicationService

    @BeforeEach
    fun setUp() {
        managementEventListener = ManagementEventListener(projectApplicationService, teamMemberApplicationService)
    }

    @Test
    fun ensureReceiveProjectCreatedEventWorksProperly() {
        // Given
        val projectId = UUID.randomUUID()
        val projectName = "OpenScrum"

        val event =
            ProjectCreated(
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

    @Test
    fun ensureReceiveDeveloperAssignedEventWorksProperly() {
        // Given
        val userId = UUID.randomUUID()
        val projectId = UUID.randomUUID()
        val event =
            DeveloperAssigned(
                userId = UserId(userId),
                projectId = ProjectId(projectId),
                username = "jdoe",
                fullName = FullName(firstName = "John", lastName = "Doe"),
            )

        // When
        managementEventListener.receiveDeveloperAssignedEvent(event)

        // Then
        verify(teamMemberApplicationService).assignDeveloper(
            AssignDeveloperCommand(
                userId = userId,
                projectId = projectId,
                username = "jdoe",
                firstName = "John",
                lastName = "Doe",
            ),
        )
    }

    @Test
    fun ensureReceiveScrumMasterAssignedEventWorksProperly() {
        // Given
        val userId = UUID.randomUUID()
        val projectId = UUID.randomUUID()
        val event =
            ScrumMasterAssigned(
                userId = UserId(userId),
                projectId = ProjectId(projectId),
                username = "mmueller",
                fullName = FullName(firstName = "Max", lastName = "Mueller"),
            )

        // When
        managementEventListener.receiveScrumMasterAssignedEvent(event)

        // Then
        verify(teamMemberApplicationService).assignScrumMaster(
            AssignScrumMasterCommand(
                userId = userId,
                projectId = projectId,
                username = "mmueller",
                firstName = "Max",
                lastName = "Mueller",
            ),
        )
    }

    @Test
    fun ensureReceiveProductOwnerAssignedEventWorksProperly() {
        // Given
        val userId = UUID.randomUUID()
        val projectId = UUID.randomUUID()
        val event =
            ProductOwnerAssigned(
                userId = UserId(userId),
                projectId = ProjectId(projectId),
                username = "jsmith",
                fullName = FullName(firstName = "Jane", lastName = "Smith"),
            )

        // When
        managementEventListener.receiveProductOwnerAssignedEvent(event)

        // Then
        verify(teamMemberApplicationService).assignProductOwner(
            AssignProductOwnerCommand(
                userId = userId,
                projectId = projectId,
                username = "jsmith",
                firstName = "Jane",
                lastName = "Smith",
            ),
        )
    }
}
