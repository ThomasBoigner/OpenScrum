package at.fhtw.openscrum.management.domain.model.project

import at.fhtw.openscrum.management.domain.model.user.EmailAddress
import at.fhtw.openscrum.management.domain.model.user.FullName
import at.fhtw.openscrum.management.domain.model.user.Role
import at.fhtw.openscrum.management.domain.model.user.User
import at.fhtw.openscrum.management.domain.model.user.UserId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class ProjectServiceTest {
    lateinit var projectService: ProjectService

    @Mock
    lateinit var projectRepository: ProjectRepository

    @BeforeEach
    fun setUp() {
        projectService = ProjectService(projectRepository)
    }

    @Test
    fun ensureGetProjectsReturnsAllProjectsForManager() {
        // Given
        val manager =
            User(
                username = "manager",
                emailAddress = EmailAddress("manager@gmail.com"),
                fullName = FullName("Manager", "User"),
                password = "password",
                role = Role.MANAGER,
            )

        val projects = listOf<Project>()
        whenever(projectRepository.findAll()).thenReturn(projects)

        // When
        val result = projectService.getProjects(manager)

        // Then
        assertThat(result).isEqualTo(projects)
    }

    @Test
    fun ensureGetProjectsReturnsOnlyUserProjectsForRegularUser() {
        // Given
        val user =
            User(
                username = "user",
                emailAddress = EmailAddress("user@gmail.com"),
                fullName = FullName("Regular", "User"),
                password = "password",
                role = Role.USER,
            )

        val projects = listOf<Project>()
        whenever(projectRepository.findProjectsOfUser(user.userId)).thenReturn(projects)

        // When
        val result = projectService.getProjects(user)

        // Then
        assertThat(result).isEqualTo(projects)
    }

    @Test
    fun ensureCreateProjectWorksProperly() {
        // Given
        val projectName = "OpenScrum"

        val manager =
            User(
                username = "manager",
                emailAddress = EmailAddress("manager@gmail.com"),
                fullName = FullName("Manager", "User"),
                password = "password",
                role = Role.MANAGER,
            )

        val productOwner =
            User(
                username = "productOwner",
                emailAddress = EmailAddress("product.owner@gmail.com"),
                fullName = FullName("Product", "Owner"),
                password = "password",
                role = Role.USER,
            )

        val scrumMaster =
            User(
                username = "scrumMaster",
                emailAddress = EmailAddress("scrum.master@gmail.com"),
                fullName = FullName("Scrum", "Master"),
                password = "password",
                role = Role.USER,
            )

        val developer =
            User(
                username = "developer",
                emailAddress = EmailAddress("developer@gmail.com"),
                fullName = FullName("Dev", "Eloper"),
                password = "password",
                role = Role.USER,
            )

        whenever(projectRepository.existsByProjectName(projectName)).thenReturn(false)
        whenever(projectRepository.save(any())).thenAnswer { it.arguments[0] }

        // When
        val project =
            projectService.createProject(
                authenticatedUser = manager,
                projectName = projectName,
                productOwner = productOwner,
                scrumMaster = scrumMaster,
                developers = setOf(developer),
            )

        // Then
        assertThat(project.projectName).isEqualTo(projectName)
        assertThat(project.productOwnerId).isEqualTo(productOwner.userId)
        assertThat(project.scrumMasterId).isEqualTo(scrumMaster.userId)
        assertThat(project.developerIds).containsExactly(developer.userId)
    }

    @Test
    fun ensureCreateProjectThrowsExceptionWhenUserIsNotAManager() {
        // Given
        val projectName = "OpenScrum"

        val user =
            User(
                username = "user",
                emailAddress = EmailAddress("user@gmail.com"),
                fullName = FullName("Regular", "User"),
                password = "password",
                role = Role.USER,
            )

        val productOwner =
            User(
                username = "productOwner",
                emailAddress = EmailAddress("product.owner@gmail.com"),
                fullName = FullName("Product", "Owner"),
                password = "password",
                role = Role.USER,
            )

        val scrumMaster =
            User(
                username = "scrumMaster",
                emailAddress = EmailAddress("scrum.master@gmail.com"),
                fullName = FullName("Scrum", "Master"),
                password = "password",
                role = Role.USER,
            )

        val developer =
            User(
                username = "developer",
                emailAddress = EmailAddress("developer@gmail.com"),
                fullName = FullName("Dev", "Eloper"),
                password = "password",
                role = Role.USER,
            )

        // When
        assertThrows<IllegalArgumentException> {
            projectService.createProject(
                authenticatedUser = user,
                projectName = projectName,
                productOwner = productOwner,
                scrumMaster = scrumMaster,
                developers = setOf(developer),
            )
        }
    }

    @Test
    fun ensureCreateProjectThrowsExceptionWhenProductOwnerIsMissing() {
        // Given
        val projectName = "OpenScrum"

        val manager =
            User(
                username = "manager",
                emailAddress = EmailAddress("manager@gmail.com"),
                fullName = FullName("Manager", "User"),
                password = "password",
                role = Role.MANAGER,
            )

        val scrumMaster =
            User(
                username = "scrumMaster",
                emailAddress = EmailAddress("scrum.master@gmail.com"),
                fullName = FullName("Scrum", "Master"),
                password = "password",
                role = Role.USER,
            )

        val developer =
            User(
                username = "developer",
                emailAddress = EmailAddress("developer@gmail.com"),
                fullName = FullName("Dev", "Eloper"),
                password = "password",
                role = Role.USER,
            )

        // When
        assertThrows<IllegalArgumentException> {
            projectService.createProject(
                authenticatedUser = manager,
                projectName = projectName,
                productOwner = null,
                scrumMaster = scrumMaster,
                developers = setOf(developer),
            )
        }
    }

    @Test
    fun ensureCreateProjectThrowsExceptionWhenScrumMasterIsMissing() {
        // Given
        val projectName = "OpenScrum"

        val manager =
            User(
                username = "manager",
                emailAddress = EmailAddress("manager@gmail.com"),
                fullName = FullName("Manager", "User"),
                password = "password",
                role = Role.MANAGER,
            )

        val productOwner =
            User(
                username = "productOwner",
                emailAddress = EmailAddress("product.owner@gmail.com"),
                fullName = FullName("Product", "Owner"),
                password = "password",
                role = Role.USER,
            )

        val developer =
            User(
                username = "developer",
                emailAddress = EmailAddress("developer@gmail.com"),
                fullName = FullName("Dev", "Eloper"),
                password = "password",
                role = Role.USER,
            )

        // When
        assertThrows<IllegalArgumentException> {
            projectService.createProject(
                authenticatedUser = manager,
                projectName = projectName,
                productOwner = productOwner,
                scrumMaster = null,
                developers = setOf(developer),
            )
        }
    }

    @Test
    fun ensureCreateProjectThrowsExceptionWhenProjectNameIsAlreadyTaken() {
        // Given
        val projectName = "OpenScrum"

        val manager =
            User(
                username = "manager",
                emailAddress = EmailAddress("manager@gmail.com"),
                fullName = FullName("Manager", "User"),
                password = "password",
                role = Role.MANAGER,
            )

        val productOwner =
            User(
                username = "productOwner",
                emailAddress = EmailAddress("product.owner@gmail.com"),
                fullName = FullName("Product", "Owner"),
                password = "password",
                role = Role.USER,
            )

        val scrumMaster =
            User(
                username = "scrumMaster",
                emailAddress = EmailAddress("scrum.master@gmail.com"),
                fullName = FullName("Scrum", "Master"),
                password = "password",
                role = Role.USER,
            )

        val developer =
            User(
                username = "developer",
                emailAddress = EmailAddress("developer@gmail.com"),
                fullName = FullName("Dev", "Eloper"),
                password = "password",
                role = Role.USER,
            )

        whenever(projectRepository.existsByProjectName(projectName)).thenReturn(true)

        // When
        assertThrows<IllegalArgumentException> {
            projectService.createProject(
                authenticatedUser = manager,
                projectName = projectName,
                productOwner = productOwner,
                scrumMaster = scrumMaster,
                developers = setOf(developer),
            )
        }
    }

    @Test
    fun ensureCreateProjectThrowsExceptionWhenProjectNameIsBlank() {
        // Given
        val projectName = ""

        val manager =
            User(
                username = "manager",
                emailAddress = EmailAddress("manager@gmail.com"),
                fullName = FullName("Manager", "User"),
                password = "password",
                role = Role.MANAGER,
            )

        val productOwner =
            User(
                username = "productOwner",
                emailAddress = EmailAddress("product.owner@gmail.com"),
                fullName = FullName("Product", "Owner"),
                password = "password",
                role = Role.USER,
            )

        val scrumMaster =
            User(
                username = "scrumMaster",
                emailAddress = EmailAddress("scrum.master@gmail.com"),
                fullName = FullName("Scrum", "Master"),
                password = "password",
                role = Role.USER,
            )

        val developer =
            User(
                username = "developer",
                emailAddress = EmailAddress("developer@gmail.com"),
                fullName = FullName("Dev", "Eloper"),
                password = "password",
                role = Role.USER,
            )

        whenever(projectRepository.existsByProjectName(projectName)).thenReturn(false)

        // When
        assertThrows<IllegalArgumentException> {
            projectService.createProject(
                authenticatedUser = manager,
                projectName = projectName,
                productOwner = productOwner,
                scrumMaster = scrumMaster,
                developers = setOf(developer),
            )
        }
    }

    @Test
    fun ensureCreateProjectThrowsExceptionWhenOneUserHasMultipleRoles() {
        // Given
        val projectName = "OpenScrum"

        val manager =
            User(
                username = "manager",
                emailAddress = EmailAddress("manager@gmail.com"),
                fullName = FullName("Manager", "User"),
                password = "password",
                role = Role.MANAGER,
            )

        val user =
            User(
                username = "user",
                emailAddress = EmailAddress("user@gmail.com"),
                fullName = FullName("Regular", "User"),
                password = "password",
                role = Role.USER,
            )

        whenever(projectRepository.existsByProjectName(projectName)).thenReturn(false)

        // When
        assertThrows<IllegalArgumentException> {
            projectService.createProject(
                authenticatedUser = manager,
                projectName = projectName,
                productOwner = user,
                scrumMaster = user,
                developers = setOf(user),
            )
        }
    }

    @Test
    fun ensureUpdateProjectWorksProperly() {
        // Given
        val projectName = "OpenScrum 2"

        val manager =
            User(
                username = "manager",
                emailAddress = EmailAddress("manager@gmail.com"),
                fullName = FullName("Manager", "User"),
                password = "password",
                role = Role.MANAGER,
            )

        val productOwner =
            User(
                username = "productOwner",
                emailAddress = EmailAddress("product.owner@gmail.com"),
                fullName = FullName("Product", "Owner"),
                password = "password",
                role = Role.USER,
            )

        val scrumMaster =
            User(
                username = "scrumMaster",
                emailAddress = EmailAddress("scrum.master@gmail.com"),
                fullName = FullName("Scrum", "Master"),
                password = "password",
                role = Role.USER,
            )

        val developer =
            User(
                username = "developer",
                emailAddress = EmailAddress("developer@gmail.com"),
                fullName = FullName("Dev", "Eloper"),
                password = "password",
                role = Role.USER,
            )

        val project =
            Project(
                projectName = "OpenScrum",
                productOwnerId = UserId(),
                scrumMasterId = UserId(),
                developerIds = setOf(UserId()),
            )

        whenever(projectRepository.findByProjectId(project.projectId)).thenReturn(project)
        whenever(projectRepository.existsByProjectName(projectName)).thenReturn(false)
        whenever(projectRepository.save(any())).thenAnswer { it.arguments[0] }

        // When
        val result =
            projectService.updateProject(
                authenticatedUser = manager,
                projectId = project.projectId,
                projectName = projectName,
                productOwner = productOwner,
                scrumMaster = scrumMaster,
                developers = setOf(developer),
            )

        // Then
        assertThat(result.projectName).isEqualTo(projectName)
        assertThat(result.productOwnerId).isEqualTo(productOwner.userId)
        assertThat(result.scrumMasterId).isEqualTo(scrumMaster.userId)
        assertThat(result.developerIds).containsExactly(developer.userId)
        verify(projectRepository).save(project)
    }

    @Test
    fun ensureUpdateProjectWorksProperlyWhenProjectKeepsItsName() {
        // Given
        val projectName = "OpenScrum"

        val manager =
            User(
                username = "manager",
                emailAddress = EmailAddress("manager@gmail.com"),
                fullName = FullName("Manager", "User"),
                password = "password",
                role = Role.MANAGER,
            )

        val productOwner =
            User(
                username = "productOwner",
                emailAddress = EmailAddress("product.owner@gmail.com"),
                fullName = FullName("Product", "Owner"),
                password = "password",
                role = Role.USER,
            )

        val scrumMaster =
            User(
                username = "scrumMaster",
                emailAddress = EmailAddress("scrum.master@gmail.com"),
                fullName = FullName("Scrum", "Master"),
                password = "password",
                role = Role.USER,
            )

        val project =
            Project(
                projectName = projectName,
                productOwnerId = productOwner.userId,
                scrumMasterId = scrumMaster.userId,
                developerIds = setOf(),
            )

        whenever(projectRepository.findByProjectId(project.projectId)).thenReturn(project)
        whenever(projectRepository.save(any())).thenAnswer { it.arguments[0] }

        // When
        val result =
            projectService.updateProject(
                authenticatedUser = manager,
                projectId = project.projectId,
                projectName = projectName,
                productOwner = productOwner,
                scrumMaster = scrumMaster,
                developers = setOf(),
            )

        // Then
        assertThat(result.projectName).isEqualTo(projectName)
        verify(projectRepository, never()).existsByProjectName(projectName)
    }

    @Test
    fun ensureUpdateProjectThrowsExceptionWhenUserIsNotAManager() {
        // Given
        val projectName = "OpenScrum 2"

        val user =
            User(
                username = "user",
                emailAddress = EmailAddress("user@gmail.com"),
                fullName = FullName("Regular", "User"),
                password = "password",
                role = Role.USER,
            )

        val productOwner =
            User(
                username = "productOwner",
                emailAddress = EmailAddress("product.owner@gmail.com"),
                fullName = FullName("Product", "Owner"),
                password = "password",
                role = Role.USER,
            )

        val scrumMaster =
            User(
                username = "scrumMaster",
                emailAddress = EmailAddress("scrum.master@gmail.com"),
                fullName = FullName("Scrum", "Master"),
                password = "password",
                role = Role.USER,
            )

        // When
        assertThrows<IllegalArgumentException> {
            projectService.updateProject(
                authenticatedUser = user,
                projectId = ProjectId(),
                projectName = projectName,
                productOwner = productOwner,
                scrumMaster = scrumMaster,
                developers = setOf(),
            )
        }
    }

    @Test
    fun ensureUpdateProjectThrowsExceptionWhenProjectDoesNotExist() {
        // Given
        val projectId = ProjectId()
        val projectName = "OpenScrum 2"

        val manager =
            User(
                username = "manager",
                emailAddress = EmailAddress("manager@gmail.com"),
                fullName = FullName("Manager", "User"),
                password = "password",
                role = Role.MANAGER,
            )

        val productOwner =
            User(
                username = "productOwner",
                emailAddress = EmailAddress("product.owner@gmail.com"),
                fullName = FullName("Product", "Owner"),
                password = "password",
                role = Role.USER,
            )

        val scrumMaster =
            User(
                username = "scrumMaster",
                emailAddress = EmailAddress("scrum.master@gmail.com"),
                fullName = FullName("Scrum", "Master"),
                password = "password",
                role = Role.USER,
            )

        whenever(projectRepository.findByProjectId(projectId)).thenReturn(null)

        // When
        val exception =
            assertThrows<IllegalArgumentException> {
                projectService.updateProject(
                    authenticatedUser = manager,
                    projectId = projectId,
                    projectName = projectName,
                    productOwner = productOwner,
                    scrumMaster = scrumMaster,
                    developers = setOf(),
                )
            }

        // Then
        assertThat(exception.message).isEqualTo("Project does not exist!")
    }

    @Test
    fun ensureUpdateProjectThrowsExceptionWhenProductOwnerIsMissing() {
        // Given
        val projectName = "OpenScrum 2"

        val manager =
            User(
                username = "manager",
                emailAddress = EmailAddress("manager@gmail.com"),
                fullName = FullName("Manager", "User"),
                password = "password",
                role = Role.MANAGER,
            )

        val scrumMaster =
            User(
                username = "scrumMaster",
                emailAddress = EmailAddress("scrum.master@gmail.com"),
                fullName = FullName("Scrum", "Master"),
                password = "password",
                role = Role.USER,
            )

        val project =
            Project(
                projectName = "OpenScrum",
                productOwnerId = UserId(),
                scrumMasterId = scrumMaster.userId,
                developerIds = setOf(),
            )

        whenever(projectRepository.findByProjectId(project.projectId)).thenReturn(project)

        // When
        assertThrows<IllegalArgumentException> {
            projectService.updateProject(
                authenticatedUser = manager,
                projectId = project.projectId,
                projectName = projectName,
                productOwner = null,
                scrumMaster = scrumMaster,
                developers = setOf(),
            )
        }
    }

    @Test
    fun ensureUpdateProjectThrowsExceptionWhenScrumMasterIsMissing() {
        // Given
        val projectName = "OpenScrum 2"

        val manager =
            User(
                username = "manager",
                emailAddress = EmailAddress("manager@gmail.com"),
                fullName = FullName("Manager", "User"),
                password = "password",
                role = Role.MANAGER,
            )

        val productOwner =
            User(
                username = "productOwner",
                emailAddress = EmailAddress("product.owner@gmail.com"),
                fullName = FullName("Product", "Owner"),
                password = "password",
                role = Role.USER,
            )

        val project =
            Project(
                projectName = "OpenScrum",
                productOwnerId = productOwner.userId,
                scrumMasterId = UserId(),
                developerIds = setOf(),
            )

        whenever(projectRepository.findByProjectId(project.projectId)).thenReturn(project)

        // When
        assertThrows<IllegalArgumentException> {
            projectService.updateProject(
                authenticatedUser = manager,
                projectId = project.projectId,
                projectName = projectName,
                productOwner = productOwner,
                scrumMaster = null,
                developers = setOf(),
            )
        }
    }

    @Test
    fun ensureUpdateProjectThrowsExceptionWhenProjectNameIsAlreadyTaken() {
        // Given
        val projectName = "Taken name"

        val manager =
            User(
                username = "manager",
                emailAddress = EmailAddress("manager@gmail.com"),
                fullName = FullName("Manager", "User"),
                password = "password",
                role = Role.MANAGER,
            )

        val productOwner =
            User(
                username = "productOwner",
                emailAddress = EmailAddress("product.owner@gmail.com"),
                fullName = FullName("Product", "Owner"),
                password = "password",
                role = Role.USER,
            )

        val scrumMaster =
            User(
                username = "scrumMaster",
                emailAddress = EmailAddress("scrum.master@gmail.com"),
                fullName = FullName("Scrum", "Master"),
                password = "password",
                role = Role.USER,
            )

        val project =
            Project(
                projectName = "OpenScrum",
                productOwnerId = productOwner.userId,
                scrumMasterId = scrumMaster.userId,
                developerIds = setOf(),
            )

        whenever(projectRepository.findByProjectId(project.projectId)).thenReturn(project)
        whenever(projectRepository.existsByProjectName(projectName)).thenReturn(true)

        // When
        assertThrows<IllegalArgumentException> {
            projectService.updateProject(
                authenticatedUser = manager,
                projectId = project.projectId,
                projectName = projectName,
                productOwner = productOwner,
                scrumMaster = scrumMaster,
                developers = setOf(),
            )
        }
    }

    @Test
    fun ensureUpdateProjectThrowsExceptionWhenOneUserHasMultipleRoles() {
        // Given
        val projectName = "OpenScrum 2"

        val manager =
            User(
                username = "manager",
                emailAddress = EmailAddress("manager@gmail.com"),
                fullName = FullName("Manager", "User"),
                password = "password",
                role = Role.MANAGER,
            )

        val user =
            User(
                username = "user",
                emailAddress = EmailAddress("user@gmail.com"),
                fullName = FullName("Regular", "User"),
                password = "password",
                role = Role.USER,
            )

        val project =
            Project(
                projectName = "OpenScrum",
                productOwnerId = user.userId,
                scrumMasterId = UserId(),
                developerIds = setOf(),
            )

        whenever(projectRepository.findByProjectId(project.projectId)).thenReturn(project)
        whenever(projectRepository.existsByProjectName(projectName)).thenReturn(false)

        // When
        assertThrows<IllegalArgumentException> {
            projectService.updateProject(
                authenticatedUser = manager,
                projectId = project.projectId,
                projectName = projectName,
                productOwner = user,
                scrumMaster = user,
                developers = setOf(user),
            )
        }
    }
}
