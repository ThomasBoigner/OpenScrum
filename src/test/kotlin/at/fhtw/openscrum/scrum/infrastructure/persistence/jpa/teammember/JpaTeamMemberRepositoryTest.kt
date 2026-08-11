package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.teammember

import at.fhtw.openscrum.scrum.domain.model.teammember.Developer
import at.fhtw.openscrum.scrum.domain.model.teammember.FullName
import at.fhtw.openscrum.scrum.domain.model.teammember.ProductOwner
import at.fhtw.openscrum.scrum.domain.model.teammember.ScrumMaster
import at.fhtw.openscrum.scrum.domain.model.teammember.TeamMemberId
import at.fhtw.openscrum.scrum.domain.model.teammember.TeamMemberRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.util.UUID

@SpringBootTest
@ActiveProfiles("postgres")
class JpaTeamMemberRepositoryTest {
    @Autowired
    lateinit var teamMemberRepository: TeamMemberRepository

    @Autowired
    lateinit var teamMemberEntityRepository: TeamMemberEntityRepository

    @BeforeEach
    fun cleanUp() {
        teamMemberEntityRepository.deleteAll()
    }

    @Test
    fun ensureFindByProjectIdAndUsernameReturnsDeveloperWhenDeveloperMatches() {
        // Given
        val projectId = UUID.randomUUID()
        teamMemberEntityRepository.save(
            DeveloperEntity(
                Developer(
                    teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
                    username = "jdoe",
                    fullName = FullName(firstName = "John", lastName = "Doe"),
                ),
            ),
        )

        // When
        val result = teamMemberRepository.findByProjectIdAndUsername(projectId, "jdoe")

        // Then
        assertThat(result).isInstanceOf(Developer::class.java)
        assertThat(result?.username).isEqualTo("jdoe")
        assertThat(result?.teamMemberId?.projectId).isEqualTo(projectId)
    }

    @Test
    fun ensureFindByProjectIdAndUsernameReturnsScrumMasterWhenScrumMasterMatches() {
        // Given
        val projectId = UUID.randomUUID()
        teamMemberEntityRepository.save(
            ScrumMasterEntity(
                ScrumMaster(
                    teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
                    username = "mmueller",
                    fullName = FullName(firstName = "Max", lastName = "Mueller"),
                ),
            ),
        )

        // When
        val result = teamMemberRepository.findByProjectIdAndUsername(projectId, "mmueller")

        // Then
        assertThat(result).isInstanceOf(ScrumMaster::class.java)
        assertThat(result?.username).isEqualTo("mmueller")
        assertThat(result?.teamMemberId?.projectId).isEqualTo(projectId)
    }

    @Test
    fun ensureFindByProjectIdAndUsernameReturnsProductOwnerWhenProductOwnerMatches() {
        // Given
        val projectId = UUID.randomUUID()
        teamMemberEntityRepository.save(
            ProductOwnerEntity(
                ProductOwner(
                    teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
                    username = "asmith",
                    fullName = FullName(firstName = "Alice", lastName = "Smith"),
                ),
            ),
        )

        // When
        val result = teamMemberRepository.findByProjectIdAndUsername(projectId, "asmith")

        // Then
        assertThat(result).isInstanceOf(ProductOwner::class.java)
        assertThat(result?.username).isEqualTo("asmith")
        assertThat(result?.teamMemberId?.projectId).isEqualTo(projectId)
    }

    @Test
    fun ensureFindByProjectIdAndUsernameReturnsNullWhenNoMatchExists() {
        // Given
        val projectId = UUID.randomUUID()

        // When
        val result = teamMemberRepository.findByProjectIdAndUsername(projectId, "unknown")

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun ensureFindAllByUserIdReturnsTeamMembersOfAllRolesAcrossProjects() {
        // Given
        val userId = UUID.randomUUID()
        val otherUserId = UUID.randomUUID()

        teamMemberEntityRepository.save(
            DeveloperEntity(
                Developer(
                    teamMemberId = TeamMemberId(userId = userId, projectId = UUID.randomUUID()),
                    username = "jdoe",
                    fullName = FullName(firstName = "John", lastName = "Doe"),
                ),
            ),
        )
        teamMemberEntityRepository.save(
            ScrumMasterEntity(
                ScrumMaster(
                    teamMemberId = TeamMemberId(userId = userId, projectId = UUID.randomUUID()),
                    username = "jdoe",
                    fullName = FullName(firstName = "John", lastName = "Doe"),
                ),
            ),
        )
        teamMemberEntityRepository.save(
            ProductOwnerEntity(
                ProductOwner(
                    teamMemberId = TeamMemberId(userId = userId, projectId = UUID.randomUUID()),
                    username = "jdoe",
                    fullName = FullName(firstName = "John", lastName = "Doe"),
                ),
            ),
        )
        teamMemberEntityRepository.save(
            DeveloperEntity(
                Developer(
                    teamMemberId = TeamMemberId(userId = otherUserId, projectId = UUID.randomUUID()),
                    username = "asmith",
                    fullName = FullName(firstName = "Alice", lastName = "Smith"),
                ),
            ),
        )

        // When
        val result = teamMemberRepository.findAllByUserId(userId)

        // Then
        assertThat(result).hasSize(3)
        assertThat(result).allMatch { it.teamMemberId.userId == userId }
        assertThat(result.filterIsInstance<Developer>()).hasSize(1)
        assertThat(result.filterIsInstance<ScrumMaster>()).hasSize(1)
        assertThat(result.filterIsInstance<ProductOwner>()).hasSize(1)
    }

    @Test
    fun ensureFindAllByUserIdReturnsEmptyListWhenUserIsNoTeamMember() {
        // Given
        val userId = UUID.randomUUID()

        // When
        val result = teamMemberRepository.findAllByUserId(userId)

        // Then
        assertThat(result).isEmpty()
    }
}
