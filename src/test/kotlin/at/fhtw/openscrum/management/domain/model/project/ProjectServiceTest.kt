package at.fhtw.openscrum.management.domain.model.project

import at.fhtw.openscrum.management.domain.model.user.EmailAddress
import at.fhtw.openscrum.management.domain.model.user.FullName
import at.fhtw.openscrum.management.domain.model.user.Role
import at.fhtw.openscrum.management.domain.model.user.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
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
}
