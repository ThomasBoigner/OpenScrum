package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.teammember

import at.fhtw.openscrum.scrum.domain.model.teammember.Developer
import at.fhtw.openscrum.scrum.domain.model.teammember.DeveloperRepository
import at.fhtw.openscrum.scrum.domain.model.teammember.FullName
import at.fhtw.openscrum.scrum.domain.model.teammember.TeamMemberId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.util.UUID

@SpringBootTest
@ActiveProfiles("postgres")
class JpaDeveloperRepositoryTest {
    @Autowired
    lateinit var developerRepository: DeveloperRepository

    @Autowired
    lateinit var developerEntityRepository: DeveloperEntityRepository

    @BeforeEach
    fun cleanUp() {
        developerEntityRepository.deleteAll()
    }

    @Test
    fun ensureFindByProjectIdWorksProperly() {
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
        developers.forEach { developerRepository.save(it) }

        // When
        val result = developerRepository.findByProjectId(projectId)

        // Then
        assertThat(result).hasSize(2)
        assertThat(result.map { it.teamMemberId }).containsExactlyInAnyOrderElementsOf(developers.map { it.teamMemberId })
        assertThat(result.map { it.username }).containsExactlyInAnyOrder("jdoe", "mmueller")
    }

    @Test
    fun ensureSaveWorksProperly() {
        // Given
        val developer =
            Developer(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = UUID.randomUUID()),
                username = "jdoe",
                fullName = FullName(firstName = "John", lastName = "Doe"),
            )

        // When
        developerRepository.save(developer)

        // Then
        val savedEntities = developerEntityRepository.findAll()
        assertThat(savedEntities).hasSize(1)
        val savedDeveloper = savedEntities.first().toDeveloper()
        assertThat(savedDeveloper.id).isNotNull
        assertThat(savedDeveloper.teamMemberId).isEqualTo(developer.teamMemberId)
        assertThat(savedDeveloper.username).isEqualTo(developer.username)
        assertThat(savedDeveloper.fullName).isEqualTo(developer.fullName)
    }
}
