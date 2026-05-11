package at.fhtw.openscrum.management.application

import at.fhtw.openscrum.management.application.command.CreateProjectCommand
import at.fhtw.openscrum.management.application.dtos.ProjectDto
import at.fhtw.openscrum.management.domain.model.project.Project
import at.fhtw.openscrum.management.domain.model.project.ProjectRepository
import at.fhtw.openscrum.management.domain.model.project.ProjectService
import at.fhtw.openscrum.management.domain.model.user.EmailAddress
import at.fhtw.openscrum.management.domain.model.user.FullName
import at.fhtw.openscrum.management.domain.model.user.Role
import at.fhtw.openscrum.management.domain.model.user.User
import at.fhtw.openscrum.management.domain.model.user.UserId
import at.fhtw.openscrum.management.domain.model.user.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class ProjectApplicationServiceTest {
    lateinit var projectApplicationService: ProjectApplicationService

    @Mock
    lateinit var projectService: ProjectService

    @Mock
    lateinit var userRepository: UserRepository

    @BeforeEach
    fun setUp() {
        projectApplicationService = ProjectApplicationService(projectService, userRepository)
    }

    @Test
    fun ensureGetProjectsWorksProperly() {
        // Given
        val username = "user"
        val user =
            User(
                username = username,
                emailAddress = EmailAddress("user@gmail.com"),
                fullName = FullName("User", "User"),
                password = "abc123",
                role = Role.MANAGER,
            )
        val project1 =
            Project(
                projectName = "OpenScrum",
                productOwnerId = UserId(),
                scrumMasterId = UserId(),
            )
        val project2 =
            Project(
                projectName = "AnotherProject",
                productOwnerId = UserId(),
                scrumMasterId = UserId(),
            )

        whenever(userRepository.findByUsername(username)).thenReturn(user)
        whenever(projectService.getProjects(user)).thenReturn(listOf(project1, project2))

        // When
        val result = projectApplicationService.getProjects(username)

        // Then
        assertThat(result).isEqualTo(listOf(ProjectDto(project1), ProjectDto(project2)))
    }

    @Test
    fun ensureCreateProjectWorksProperly() {
        // Given
        val manager =
            User(
                username = "manager",
                emailAddress = EmailAddress("manager@gmail.com"),
                fullName = FullName("Manager", "User"),
                password = "abc123",
                role = Role.MANAGER,
            )

        val productOwner =
            User(
                username = "product.owner",
                emailAddress = EmailAddress("product.owner@gmail.com"),
                fullName = FullName("Product", "Owner"),
                password = "abc123",
                role = Role.USER,
            )

        val scrumMaster =
            User(
                username = "scrum.master",
                emailAddress = EmailAddress("scrum.master@gmail.com"),
                fullName = FullName("Scrum", "Master"),
                password = "abc123",
                role = Role.USER,
            )

        val developer =
            User(
                username = "developer",
                emailAddress = EmailAddress("developer@gmail.com"),
                fullName = FullName("Developer", "User"),
                password = "abc123",
                role = Role.USER,
            )

        val command =
            CreateProjectCommand(
                projectName = "OpenScrum",
                productOwnerId = productOwner.userId.token,
                scrumMasterId = scrumMaster.userId.token,
                developerIds = setOf(developer.userId.token),
            )

        val expectedProject =
            Project(
                projectName = command.projectName,
                productOwnerId = productOwner.userId,
                scrumMasterId = scrumMaster.userId,
                developerIds = setOf(developer.userId),
            )

        whenever(userRepository.findByUsername(manager.username)).thenReturn(manager)
        whenever(userRepository.findByUserId(productOwner.userId)).thenReturn(productOwner)
        whenever(userRepository.findByUserId(scrumMaster.userId)).thenReturn(scrumMaster)
        whenever(userRepository.findByUserId(developer.userId)).thenReturn(developer)
        whenever(
            projectService.createProject(
                authenticatedUser = manager,
                projectName = command.projectName,
                productOwner = productOwner,
                scrumMaster = scrumMaster,
                developers = setOf(developer),
            ),
        ).thenReturn(expectedProject)

        // When
        val projectDto = projectApplicationService.createProject(manager.username, command)

        // Then
        assertThat(projectDto.projectId).isEqualTo(expectedProject.projectId.token)
        assertThat(projectDto.projectName).isEqualTo(expectedProject.projectName)
        assertThat(projectDto.developerIds).hasSize(expectedProject.developerIds.size)
    }

    @Test
    fun ensureCreateProjectThrowsExceptionIfAuthenticatedUserCanNotBeFound() {
        // Given
        val authenticatedUserUsername = "manager"

        val command =
            CreateProjectCommand(
                projectName = "OpenScrum",
                productOwnerId = UserId().token,
                scrumMasterId = UserId().token,
                developerIds = setOf(),
            )

        whenever(userRepository.findByUsername(authenticatedUserUsername)).thenReturn(null)

        // When / Then
        assertThrows<IllegalArgumentException> {
            projectApplicationService.createProject(authenticatedUserUsername, command)
        }
    }
}
