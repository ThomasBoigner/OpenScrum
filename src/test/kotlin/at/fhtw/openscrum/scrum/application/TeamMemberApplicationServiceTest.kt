package at.fhtw.openscrum.scrum.application

import at.fhtw.openscrum.scrum.application.command.AssignDeveloperCommand
import at.fhtw.openscrum.scrum.application.command.AssignProductOwnerCommand
import at.fhtw.openscrum.scrum.application.command.AssignScrumMasterCommand
import at.fhtw.openscrum.scrum.application.dtos.DeveloperDto
import at.fhtw.openscrum.scrum.domain.model.teammember.Developer
import at.fhtw.openscrum.scrum.domain.model.teammember.DeveloperRepository
import at.fhtw.openscrum.scrum.domain.model.teammember.FullName
import at.fhtw.openscrum.scrum.domain.model.teammember.ProductOwner
import at.fhtw.openscrum.scrum.domain.model.teammember.ProductOwnerRepository
import at.fhtw.openscrum.scrum.domain.model.teammember.ScrumMaster
import at.fhtw.openscrum.scrum.domain.model.teammember.ScrumMasterRepository
import at.fhtw.openscrum.scrum.domain.model.teammember.TeamMemberId
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
        teamMemberApplicationService =
            TeamMemberApplicationService(developerRepository, scrumMasterRepository, productOwnerRepository)
    }

    @Test
    fun ensureGetDevelopersOfProjectWorksProperly() {
        // Given
        val projectId = UUID.randomUUID()
        val developers =
            listOf(
                Developer(
                    teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
                    username = "jdoe",
                    fullName = FullName(firstName = "John", lastName = "Doe"),
                ),
                Developer(
                    teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
                    username = "mmueller",
                    fullName = FullName(firstName = "Max", lastName = "Mueller"),
                ),
            )

        whenever(developerRepository.findByProjectId(projectId)).thenReturn(developers)

        // When
        val result = teamMemberApplicationService.getDevelopersOfProject(projectId)

        // Then
        assertThat(result).hasSize(2)
        assertThat(result[0]).isEqualTo(DeveloperDto(developers[0]))
        assertThat(result[1]).isEqualTo(DeveloperDto(developers[1]))
    }

    @Test
    fun ensureGetDevelopersOfProjectReturnsEmptyListWhenNoDevelopersExist() {
        // Given
        val projectId = UUID.randomUUID()

        whenever(developerRepository.findByProjectId(projectId)).thenReturn(emptyList())

        // When
        val result = teamMemberApplicationService.getDevelopersOfProject(projectId)

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun ensureGetScrumMasterOfProjectWorksProperly() {
        // Given
        val projectId = UUID.randomUUID()
        val scrumMaster =
            ScrumMaster(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
                username = "mmueller",
                fullName = FullName(firstName = "Max", lastName = "Mueller"),
            )

        whenever(scrumMasterRepository.findByProjectId(projectId)).thenReturn(scrumMaster)

        // When
        val result = teamMemberApplicationService.getScrumMasterOfProject(projectId)

        // Then
        assertThat(result).isNotNull
        assertThat(result!!.userId).isEqualTo(scrumMaster.teamMemberId.userId)
        assertThat(result.projectId).isEqualTo(projectId)
        assertThat(result.username).isEqualTo(scrumMaster.username)
        assertThat(result.firstName).isEqualTo(scrumMaster.fullName.firstName)
        assertThat(result.lastName).isEqualTo(scrumMaster.fullName.lastName)
        assertThat(result.fullName).isEqualTo("${scrumMaster.fullName.firstName} ${scrumMaster.fullName.lastName}")
    }

    @Test
    fun ensureGetScrumMasterOfProjectReturnsNullWhenNotFound() {
        // Given
        val projectId = UUID.randomUUID()

        whenever(scrumMasterRepository.findByProjectId(projectId)).thenReturn(null)

        // When
        val result = teamMemberApplicationService.getScrumMasterOfProject(projectId)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun ensureGetProductOwnerOfProjectWorksProperly() {
        // Given
        val projectId = UUID.randomUUID()
        val productOwner =
            ProductOwner(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
                username = "jsmith",
                fullName = FullName(firstName = "Jane", lastName = "Smith"),
            )

        whenever(productOwnerRepository.findByProjectId(projectId)).thenReturn(productOwner)

        // When
        val result = teamMemberApplicationService.getProductOwnerOfProject(projectId)

        // Then
        assertThat(result).isNotNull
        assertThat(result!!.userId).isEqualTo(productOwner.teamMemberId.userId)
        assertThat(result.projectId).isEqualTo(projectId)
        assertThat(result.username).isEqualTo(productOwner.username)
        assertThat(result.firstName).isEqualTo(productOwner.fullName.firstName)
        assertThat(result.lastName).isEqualTo(productOwner.fullName.lastName)
        assertThat(result.fullName).isEqualTo("${productOwner.fullName.firstName} ${productOwner.fullName.lastName}")
    }

    @Test
    fun ensureGetProductOwnerOfProjectReturnsNullWhenNotFound() {
        // Given
        val projectId = UUID.randomUUID()

        whenever(productOwnerRepository.findByProjectId(projectId)).thenReturn(null)

        // When
        val result = teamMemberApplicationService.getProductOwnerOfProject(projectId)

        // Then
        assertThat(result).isNull()
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
