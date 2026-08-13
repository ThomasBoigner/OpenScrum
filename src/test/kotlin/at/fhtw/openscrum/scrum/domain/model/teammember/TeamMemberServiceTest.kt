package at.fhtw.openscrum.scrum.domain.model.teammember

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
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class TeamMemberServiceTest {
    lateinit var teamMemberService: TeamMemberService

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
        teamMemberService =
            TeamMemberService(teamMemberRepository, developerRepository, scrumMasterRepository, productOwnerRepository)
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

        // When
        teamMemberService.updateTeamMemberInformation(
            userId = userId,
            username = "jane.doe",
            firstName = "Jane",
            lastName = "Doe",
        )

        // Then
        verify(developerRepository).save(developer)
        assertThat(developer.id).isEqualTo(1)
        assertThat(developer.teamMemberId).isEqualTo(TeamMemberId(userId = userId, projectId = developerProjectId))
        assertThat(developer.username).isEqualTo("jane.doe")
        assertThat(developer.fullName).isEqualTo(FullName(firstName = "Jane", lastName = "Doe"))

        verify(scrumMasterRepository).save(scrumMaster)
        assertThat(scrumMaster.id).isEqualTo(2)
        assertThat(scrumMaster.teamMemberId).isEqualTo(TeamMemberId(userId = userId, projectId = scrumMasterProjectId))
        assertThat(scrumMaster.username).isEqualTo("jane.doe")
        assertThat(scrumMaster.fullName).isEqualTo(FullName(firstName = "Jane", lastName = "Doe"))

        verify(productOwnerRepository).save(productOwner)
        assertThat(productOwner.id).isEqualTo(3)
        assertThat(productOwner.teamMemberId).isEqualTo(TeamMemberId(userId = userId, projectId = productOwnerProjectId))
        assertThat(productOwner.username).isEqualTo("jane.doe")
        assertThat(productOwner.fullName).isEqualTo(FullName(firstName = "Jane", lastName = "Doe"))
    }

    @Test
    fun ensureUpdateTeamMemberInformationDoesNothingWhenUserIsNoTeamMember() {
        // Given
        val userId = UUID.randomUUID()

        whenever(teamMemberRepository.findAllByUserId(userId)).thenReturn(emptyList())

        // When
        teamMemberService.updateTeamMemberInformation(
            userId = userId,
            username = "jane.doe",
            firstName = "Jane",
            lastName = "Doe",
        )

        // Then
        verify(developerRepository, never()).save(any())
        verify(scrumMasterRepository, never()).save(any())
        verify(productOwnerRepository, never()).save(any())
    }

    @Test
    fun ensureUpdateTeamMemberInformationThrowsExceptionWhenNameIsBlank() {
        // Given
        val userId = UUID.randomUUID()

        // When
        assertThrows<IllegalArgumentException> {
            teamMemberService.updateTeamMemberInformation(
                userId = userId,
                username = "jane.doe",
                firstName = "",
                lastName = "Doe",
            )
        }
    }
}
