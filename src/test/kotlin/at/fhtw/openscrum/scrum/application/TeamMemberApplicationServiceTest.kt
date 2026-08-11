package at.fhtw.openscrum.scrum.application

import at.fhtw.openscrum.scrum.application.command.AssignDeveloperCommand
import at.fhtw.openscrum.scrum.application.command.AssignProductOwnerCommand
import at.fhtw.openscrum.scrum.application.command.AssignScrumMasterCommand
import at.fhtw.openscrum.scrum.application.command.UnassignDeveloperCommand
import at.fhtw.openscrum.scrum.application.command.UnassignProductOwnerCommand
import at.fhtw.openscrum.scrum.application.command.UnassignScrumMasterCommand
import at.fhtw.openscrum.scrum.application.command.UpdateTeamMemberInformationCommand
import at.fhtw.openscrum.scrum.application.dtos.DeveloperDto
import at.fhtw.openscrum.scrum.domain.model.teammember.Developer
import at.fhtw.openscrum.scrum.domain.model.teammember.DeveloperRepository
import at.fhtw.openscrum.scrum.domain.model.teammember.FullName
import at.fhtw.openscrum.scrum.domain.model.teammember.ProductOwner
import at.fhtw.openscrum.scrum.domain.model.teammember.ProductOwnerRepository
import at.fhtw.openscrum.scrum.domain.model.teammember.ScrumMaster
import at.fhtw.openscrum.scrum.domain.model.teammember.ScrumMasterRepository
import at.fhtw.openscrum.scrum.domain.model.teammember.TeamMemberId
import at.fhtw.openscrum.scrum.domain.model.teammember.TeamMemberRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class TeamMemberApplicationServiceTest {
    lateinit var teamMemberApplicationService: TeamMemberApplicationService

    @Mock
    lateinit var teamMemberRepository: TeamMemberRepository

    @Mock
    lateinit var developerRepository: DeveloperRepository

    @Mock
    lateinit var scrumMasterRepository: ScrumMasterRepository

    @Mock
    lateinit var productOwnerRepository: ProductOwnerRepository

    @BeforeEach
    fun setUp() {
        teamMemberApplicationService =
            TeamMemberApplicationService(teamMemberRepository, developerRepository, scrumMasterRepository, productOwnerRepository)
    }

    @Test
    fun ensureGetTeamMemberOfProjectWorksProperly() {
        // Given
        val projectId = UUID.randomUUID()
        val developer =
            Developer(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
                username = "jdoe",
                fullName = FullName(firstName = "John", lastName = "Doe"),
            )

        whenever(teamMemberRepository.findByProjectIdAndUsername(projectId, "jdoe")).thenReturn(developer)

        // When
        val result = teamMemberApplicationService.getTeamMemberOfProject(projectId, "jdoe")

        // Then
        assertThat(result).isNotNull
        assertThat(result!!.userId).isEqualTo(developer.teamMemberId.userId)
        assertThat(result.projectId).isEqualTo(projectId)
        assertThat(result.username).isEqualTo(developer.username)
        assertThat(result.firstName).isEqualTo(developer.fullName.firstName)
        assertThat(result.lastName).isEqualTo(developer.fullName.lastName)
    }

    @Test
    fun ensureGetTeamMemberOfProjectReturnsNullWhenNotFound() {
        // Given
        val projectId = UUID.randomUUID()

        whenever(teamMemberRepository.findByProjectIdAndUsername(projectId, "unknown")).thenReturn(null)

        // When
        val result = teamMemberApplicationService.getTeamMemberOfProject(projectId, "unknown")

        // Then
        assertThat(result).isNull()
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

    @Test
    fun ensureUpdateTeamMemberInformationWorksProperly() {
        // Given
        val userId = UUID.randomUUID()
        val developerProjectId = UUID.randomUUID()
        val scrumMasterProjectId = UUID.randomUUID()
        val productOwnerProjectId = UUID.randomUUID()

        val developer =
            Developer(
                id = 1,
                teamMemberId = TeamMemberId(userId = userId, projectId = developerProjectId),
                username = "jdoe",
                fullName = FullName(firstName = "John", lastName = "Doe"),
            )
        val scrumMaster =
            ScrumMaster(
                id = 2,
                teamMemberId = TeamMemberId(userId = userId, projectId = scrumMasterProjectId),
                username = "jdoe",
                fullName = FullName(firstName = "John", lastName = "Doe"),
            )
        val productOwner =
            ProductOwner(
                id = 3,
                teamMemberId = TeamMemberId(userId = userId, projectId = productOwnerProjectId),
                username = "jdoe",
                fullName = FullName(firstName = "John", lastName = "Doe"),
            )

        whenever(teamMemberRepository.findAllByUserId(userId))
            .thenReturn(listOf(developer, scrumMaster, productOwner))

        val command =
            UpdateTeamMemberInformationCommand(
                userId = userId,
                username = "jane.doe",
                firstName = "Jane",
                lastName = "Doe",
            )

        // When
        teamMemberApplicationService.updateTeamMemberInformation(command)

        // Then
        val developerCaptor = argumentCaptor<Developer>()
        verify(developerRepository).save(developerCaptor.capture())
        assertThat(developerCaptor.firstValue.id).isEqualTo(developer.id)
        assertThat(developerCaptor.firstValue.teamMemberId).isEqualTo(developer.teamMemberId)
        assertThat(developerCaptor.firstValue.username).isEqualTo("jane.doe")
        assertThat(developerCaptor.firstValue.fullName).isEqualTo(FullName(firstName = "Jane", lastName = "Doe"))

        val scrumMasterCaptor = argumentCaptor<ScrumMaster>()
        verify(scrumMasterRepository).save(scrumMasterCaptor.capture())
        assertThat(scrumMasterCaptor.firstValue.id).isEqualTo(scrumMaster.id)
        assertThat(scrumMasterCaptor.firstValue.teamMemberId).isEqualTo(scrumMaster.teamMemberId)
        assertThat(scrumMasterCaptor.firstValue.username).isEqualTo("jane.doe")
        assertThat(scrumMasterCaptor.firstValue.fullName).isEqualTo(FullName(firstName = "Jane", lastName = "Doe"))

        val productOwnerCaptor = argumentCaptor<ProductOwner>()
        verify(productOwnerRepository).save(productOwnerCaptor.capture())
        assertThat(productOwnerCaptor.firstValue.id).isEqualTo(productOwner.id)
        assertThat(productOwnerCaptor.firstValue.teamMemberId).isEqualTo(productOwner.teamMemberId)
        assertThat(productOwnerCaptor.firstValue.username).isEqualTo("jane.doe")
        assertThat(productOwnerCaptor.firstValue.fullName).isEqualTo(FullName(firstName = "Jane", lastName = "Doe"))
    }

    @Test
    fun ensureUpdateTeamMemberInformationDoesNothingWhenUserIsNoTeamMember() {
        // Given
        val userId = UUID.randomUUID()

        whenever(teamMemberRepository.findAllByUserId(userId)).thenReturn(emptyList())

        val command =
            UpdateTeamMemberInformationCommand(
                userId = userId,
                username = "jane.doe",
                firstName = "Jane",
                lastName = "Doe",
            )

        // When
        teamMemberApplicationService.updateTeamMemberInformation(command)

        // Then
        verify(developerRepository, never()).save(any())
        verify(scrumMasterRepository, never()).save(any())
        verify(productOwnerRepository, never()).save(any())
    }

    @Test
    fun ensureUnassignDeveloperWorksProperly() {
        // Given
        val command =
            UnassignDeveloperCommand(
                userId = UUID.randomUUID(),
                projectId = UUID.randomUUID(),
            )

        // When
        teamMemberApplicationService.unassignDeveloper(command)

        // Then
        verify(developerRepository).delete(TeamMemberId(userId = command.userId, projectId = command.projectId))
    }

    @Test
    fun ensureUnassignScrumMasterWorksProperly() {
        // Given
        val command =
            UnassignScrumMasterCommand(
                userId = UUID.randomUUID(),
                projectId = UUID.randomUUID(),
            )

        // When
        teamMemberApplicationService.unassignScrumMaster(command)

        // Then
        verify(scrumMasterRepository).delete(TeamMemberId(userId = command.userId, projectId = command.projectId))
    }

    @Test
    fun ensureUnassignProductOwnerWorksProperly() {
        // Given
        val command =
            UnassignProductOwnerCommand(
                userId = UUID.randomUUID(),
                projectId = UUID.randomUUID(),
            )

        // When
        teamMemberApplicationService.unassignProductOwner(command)

        // Then
        verify(productOwnerRepository).delete(TeamMemberId(userId = command.userId, projectId = command.projectId))
    }
}
