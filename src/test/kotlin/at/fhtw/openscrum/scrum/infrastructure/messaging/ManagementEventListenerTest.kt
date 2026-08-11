package at.fhtw.openscrum.scrum.infrastructure.messaging

import at.fhtw.openscrum.management.domain.model.project.DeveloperAssigned
import at.fhtw.openscrum.management.domain.model.project.DeveloperUnassigned
import at.fhtw.openscrum.management.domain.model.project.ProductOwnerAssigned
import at.fhtw.openscrum.management.domain.model.project.ProductOwnerUnassigned
import at.fhtw.openscrum.management.domain.model.project.ProjectCanceled
import at.fhtw.openscrum.management.domain.model.project.ProjectCreated
import at.fhtw.openscrum.management.domain.model.project.ProjectId
import at.fhtw.openscrum.management.domain.model.project.ProjectInformationChanged
import at.fhtw.openscrum.management.domain.model.project.ScrumMasterAssigned
import at.fhtw.openscrum.management.domain.model.project.ScrumMasterUnassigned
import at.fhtw.openscrum.management.domain.model.user.EmailAddress
import at.fhtw.openscrum.management.domain.model.user.FullName
import at.fhtw.openscrum.management.domain.model.user.UserId
import at.fhtw.openscrum.management.domain.model.user.UserInformationChanged
import at.fhtw.openscrum.scrum.application.ProjectApplicationService
import at.fhtw.openscrum.scrum.application.TeamMemberApplicationService
import at.fhtw.openscrum.scrum.application.command.AssignDeveloperCommand
import at.fhtw.openscrum.scrum.application.command.AssignProductOwnerCommand
import at.fhtw.openscrum.scrum.application.command.AssignScrumMasterCommand
import at.fhtw.openscrum.scrum.application.command.CancelProjectCommand
import at.fhtw.openscrum.scrum.application.command.CreateProjectCommand
import at.fhtw.openscrum.scrum.application.command.UnassignDeveloperCommand
import at.fhtw.openscrum.scrum.application.command.UnassignProductOwnerCommand
import at.fhtw.openscrum.scrum.application.command.UnassignScrumMasterCommand
import at.fhtw.openscrum.scrum.application.command.UpdateProjectCommand
import at.fhtw.openscrum.scrum.application.command.UpdateTeamMemberInformationCommand
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
    fun ensureReceiveProjectInformationChangedEventWorksProperly() {
        // Given
        val projectId = UUID.randomUUID()
        val projectName = "OpenScrum 2"

        val event =
            ProjectInformationChanged(
                projectId = ProjectId(projectId),
                projectName = projectName,
            )

        // When
        managementEventListener.receiveProjectInformationChangedEvent(event)

        // Then
        verify(projectApplicationService).updateProject(
            UpdateProjectCommand(
                projectId = projectId,
                projectName = projectName,
            ),
        )
    }

    @Test
    fun ensureReceiveProjectCanceledEventWorksProperly() {
        // Given
        val projectId = UUID.randomUUID()

        val event = ProjectCanceled(projectId = ProjectId(projectId))

        // When
        managementEventListener.receiveProjectCanceledEvent(event)

        // Then
        verify(projectApplicationService).cancelProject(
            CancelProjectCommand(
                projectId = projectId,
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

    @Test
    fun ensureReceiveProductOwnerUnassignedEventWorksProperly() {
        // Given
        val userId = UUID.randomUUID()
        val projectId = UUID.randomUUID()
        val event =
            ProductOwnerUnassigned(
                userId = UserId(userId),
                projectId = ProjectId(projectId),
            )

        // When
        managementEventListener.receiveProductOwnerUnassignedEvent(event)

        // Then
        verify(teamMemberApplicationService).unassignProductOwner(
            UnassignProductOwnerCommand(
                userId = userId,
                projectId = projectId,
            ),
        )
    }

    @Test
    fun ensureReceiveScrumMasterUnassignedEventWorksProperly() {
        // Given
        val userId = UUID.randomUUID()
        val projectId = UUID.randomUUID()
        val event =
            ScrumMasterUnassigned(
                userId = UserId(userId),
                projectId = ProjectId(projectId),
            )

        // When
        managementEventListener.receiveScrumMasterUnassignedEvent(event)

        // Then
        verify(teamMemberApplicationService).unassignScrumMaster(
            UnassignScrumMasterCommand(
                userId = userId,
                projectId = projectId,
            ),
        )
    }

    @Test
    fun ensureReceiveUserInformationChangedEventWorksProperly() {
        // Given
        val userId = UUID.randomUUID()
        val username = "jane.doe"
        val emailAddress = "jane.doe@gmail.com"
        val firstName = "Jane"
        val lastName = "Doe"

        val event =
            UserInformationChanged(
                userId = UserId(userId),
                username = username,
                emailAddress = EmailAddress(emailAddress),
                fullName = FullName(firstName, lastName),
            )

        // When
        managementEventListener.receiveUserInformationChangedEvent(event)

        // Then
        verify(teamMemberApplicationService).updateTeamMemberInformation(
            UpdateTeamMemberInformationCommand(
                userId = userId,
                username = username,
                firstName = firstName,
                lastName = lastName,
            ),
        )
    }

    @Test
    fun ensureReceiveDeveloperUnassignedEventWorksProperly() {
        // Given
        val userId = UUID.randomUUID()
        val projectId = UUID.randomUUID()
        val event =
            DeveloperUnassigned(
                userId = UserId(userId),
                projectId = ProjectId(projectId),
            )

        // When
        managementEventListener.receiveDeveloperUnassignedEvent(event)

        // Then
        verify(teamMemberApplicationService).unassignDeveloper(
            UnassignDeveloperCommand(
                userId = userId,
                projectId = projectId,
            ),
        )
    }
}
