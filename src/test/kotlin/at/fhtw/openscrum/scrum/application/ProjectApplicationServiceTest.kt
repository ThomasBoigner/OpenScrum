package at.fhtw.openscrum.scrum.application

import at.fhtw.openscrum.scrum.application.command.CreateProjectCommand
import at.fhtw.openscrum.scrum.domain.model.project.Project
import at.fhtw.openscrum.scrum.domain.model.project.ProjectId
import at.fhtw.openscrum.scrum.domain.model.project.ProjectRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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

    @BeforeEach
    fun setUp() {
        projectApplicationService = ProjectApplicationService(projectRepository)
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
}
