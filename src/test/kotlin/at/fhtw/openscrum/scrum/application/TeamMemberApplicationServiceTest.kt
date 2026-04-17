package at.fhtw.openscrum.scrum.application

import at.fhtw.openscrum.scrum.application.command.AssignDeveloperCommand
import at.fhtw.openscrum.scrum.application.command.AssignProductOwnerCommand
import at.fhtw.openscrum.scrum.application.command.AssignScrumMasterCommand
import at.fhtw.openscrum.scrum.domain.model.teammember.DeveloperRepository
import at.fhtw.openscrum.scrum.domain.model.teammember.ProductOwnerRepository
import at.fhtw.openscrum.scrum.domain.model.teammember.ScrumMasterRepository
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
class TeamMemberApplicationServiceTest {
    lateinit var teamMemberApplicationService: TeamMemberApplicationService

    @Mock
    lateinit var developerRepository: DeveloperRepository

    @Mock
    lateinit var scrumMasterRepository: ScrumMasterRepository

    @Mock
    lateinit var productOwnerRepository: ProductOwnerRepository

    @BeforeEach
    fun setUp() {
        teamMemberApplicationService = TeamMemberApplicationService(developerRepository, scrumMasterRepository, productOwnerRepository)
    }

    @Test
    fun ensureAssignDeveloperWorksProperly() {
        // Given
        val command =
            AssignDeveloperCommand(
                userId = UUID.randomUUID(),
                projectId = UUID.randomUUID(),
                username = "jdoe",
                firstName = "John",
                lastName = "Doe",
            )

        whenever(developerRepository.save(any())).thenAnswer { it.arguments[0] }

        // When
        val result = teamMemberApplicationService.assignDeveloper(command)

        // Then
        assertThat(result.userId).isEqualTo(command.userId)
        assertThat(result.projectId).isEqualTo(command.projectId)
        assertThat(result.username).isEqualTo(command.username)
        assertThat(result.firstName).isEqualTo(command.firstName)
        assertThat(result.lastName).isEqualTo(command.lastName)
        assertThat(result.fullName).isEqualTo("${command.firstName} ${command.lastName}")
    }

    @Test
    fun ensureAssignScrumMasterWorksProperly() {
        // Given
        val command =
            AssignScrumMasterCommand(
                userId = UUID.randomUUID(),
                projectId = UUID.randomUUID(),
                username = "mmueller",
                firstName = "Max",
                lastName = "Mueller",
            )

        whenever(scrumMasterRepository.save(any())).thenAnswer { it.arguments[0] }

        // When
        val result = teamMemberApplicationService.assignScrumMaster(command)

        // Then
        assertThat(result.userId).isEqualTo(command.userId)
        assertThat(result.projectId).isEqualTo(command.projectId)
        assertThat(result.username).isEqualTo(command.username)
        assertThat(result.firstName).isEqualTo(command.firstName)
        assertThat(result.lastName).isEqualTo(command.lastName)
        assertThat(result.fullName).isEqualTo("${command.firstName} ${command.lastName}")
    }

    @Test
    fun ensureAssignProductOwnerWorksProperly() {
        // Given
        val command =
            AssignProductOwnerCommand(
                userId = UUID.randomUUID(),
                projectId = UUID.randomUUID(),
                username = "jsmith",
                firstName = "Jane",
                lastName = "Smith",
            )

        whenever(productOwnerRepository.save(any())).thenAnswer { it.arguments[0] }

        // When
        val result = teamMemberApplicationService.assignProductOwner(command)

        // Then
        assertThat(result.userId).isEqualTo(command.userId)
        assertThat(result.projectId).isEqualTo(command.projectId)
        assertThat(result.username).isEqualTo(command.username)
        assertThat(result.firstName).isEqualTo(command.firstName)
        assertThat(result.lastName).isEqualTo(command.lastName)
        assertThat(result.fullName).isEqualTo("${command.firstName} ${command.lastName}")
    }
}
