package at.fhtw.openscrum.scrum.application

import at.fhtw.openscrum.scrum.application.command.CreateProjectCommand
import at.fhtw.openscrum.scrum.application.command.DefineProductGoalCommand
import at.fhtw.openscrum.scrum.application.command.DefineSprintLengthCommand
import at.fhtw.openscrum.scrum.domain.model.project.Project
import at.fhtw.openscrum.scrum.domain.model.project.ProjectId
import at.fhtw.openscrum.scrum.domain.model.project.ProjectRepository
import at.fhtw.openscrum.scrum.domain.model.teammember.FullName
import at.fhtw.openscrum.scrum.domain.model.teammember.ProductOwner
import at.fhtw.openscrum.scrum.domain.model.teammember.ProductOwnerRepository
import at.fhtw.openscrum.scrum.domain.model.teammember.ScrumMaster
import at.fhtw.openscrum.scrum.domain.model.teammember.ScrumMasterRepository
import at.fhtw.openscrum.scrum.domain.model.teammember.TeamMemberId
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class ProjectApplicationServiceTest {
    lateinit var projectApplicationService: ProjectApplicationService

    @Mock
    lateinit var projectRepository: ProjectRepository

    @Mock
    lateinit var scrumMasterRepository: ScrumMasterRepository

    @Mock
    lateinit var productOwnerRepository: ProductOwnerRepository

    @BeforeEach
    fun setUp() {
        projectApplicationService = ProjectApplicationService(projectRepository, scrumMasterRepository, productOwnerRepository)
    }

    @Test
    fun ensureGetProjectWorksProperly() {
        // Given
        val projectId = UUID.randomUUID()
        val project =
            Project(
                projectId = ProjectId(projectId),
                projectName = "projectName",
            )

        whenever(projectRepository.findByProjectId(ProjectId(projectId))).thenReturn(project)

        // When
        val result = projectApplicationService.getProject(projectId)

        // Then
        assertThat(result).isNotNull
        assertThat(result!!.projectId).isEqualTo(projectId)
        assertThat(result.projectName).isEqualTo(project.projectName)
    }

    @Test
    fun ensureGetProjectReturnsNullWhenProjectDoesNotExist() {
        // Given
        val projectId = UUID.randomUUID()

        whenever(projectRepository.findByProjectId(ProjectId(projectId))).thenReturn(null)

        // When
        val result = projectApplicationService.getProject(projectId)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun ensureCreateProjectWorksProperly() {
        // Given
        val command =
            CreateProjectCommand(
                projectId = UUID.randomUUID(),
                projectName = "projectName",
            )

        whenever(projectRepository.save(any())).thenAnswer { it.arguments[0] }

        // When
        val result = projectApplicationService.createProject(command)

        // Then
        assertThat(result.projectId).isEqualTo(command.projectId)
        assertThat(result.projectName).isEqualTo(command.projectName)
    }

    @Test
    fun ensureDefineSprintLengthWorksProperly() {
        // Given
        val projectId = UUID.randomUUID()
        val username = "scrummaster"
        val command = DefineSprintLengthCommand(projectId = projectId, sprintLength = 3)
        val project = Project(projectId = ProjectId(projectId), projectName = "Test Project")
        val scrumMaster =
            ScrumMaster(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
                username = username,
                fullName = FullName("First", "Last"),
            )

        whenever(projectRepository.findByProjectId(ProjectId(projectId))).thenReturn(project)
        whenever(scrumMasterRepository.findByUsername(username)).thenReturn(scrumMaster)
        whenever(projectRepository.save(any())).thenAnswer { it.arguments[0] }

        // When
        val result = projectApplicationService.defineSprintLength(username, command)

        // Then
        assertThat(result.sprintLength).isEqualTo(3)
    }

    @Test
    fun ensureDefineSprintLengthThrowsWhenProjectNotFound() {
        // Given
        val command = DefineSprintLengthCommand(projectId = UUID.randomUUID(), sprintLength = 3)

        whenever(projectRepository.findByProjectId(any())).thenReturn(null)

        // When
        assertThrows<IllegalArgumentException> { projectApplicationService.defineSprintLength("scrummaster", command) }
    }

    @Test
    fun ensureDefineProductGoalWorksProperly() {
        // Given
        val projectId = UUID.randomUUID()
        val username = "productowner"
        val command = DefineProductGoalCommand(projectId = projectId, productGoal = "Deliver MVP")
        val project = Project(projectId = ProjectId(projectId), projectName = "Test Project")
        val productOwner =
            ProductOwner(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
                username = username,
                fullName = FullName("First", "Last"),
            )

        whenever(projectRepository.findByProjectId(ProjectId(projectId))).thenReturn(project)
        whenever(productOwnerRepository.findByUsername(username)).thenReturn(productOwner)
        whenever(projectRepository.save(any())).thenAnswer { it.arguments[0] }

        // When
        val result = projectApplicationService.defineProductGoal(username, command)

        // Then
        assertThat(result.productGoal).isEqualTo("Deliver MVP")
    }

    @Test
    fun ensureDefineProductGoalThrowsWhenProjectNotFound() {
        // Given
        val command = DefineProductGoalCommand(projectId = UUID.randomUUID(), productGoal = "Deliver MVP")

        whenever(projectRepository.findByProjectId(any())).thenReturn(null)

        // When
        assertThrows<IllegalArgumentException> { projectApplicationService.defineProductGoal("productowner", command) }
    }
}
