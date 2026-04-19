package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.teammember

import at.fhtw.openscrum.scrum.domain.model.teammember.FullName
import at.fhtw.openscrum.scrum.domain.model.teammember.ScrumMaster
import at.fhtw.openscrum.scrum.domain.model.teammember.ScrumMasterRepository
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
class JpaScrumMasterRepositoryTest {
    @Autowired
    lateinit var scrumMasterRepository: ScrumMasterRepository

    @Autowired
    lateinit var scrumMasterEntityRepository: ScrumMasterEntityRepository

    @BeforeEach
    fun cleanUp() {
        scrumMasterEntityRepository.deleteAll()
    }

    @Test
    fun ensureFindByProjectIdWorksProperly() {
        // Given
        val projectId = UUID.randomUUID()
        val scrumMaster =
            ScrumMaster(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
                username = "mmueller",
                fullName = FullName(firstName = "Max", lastName = "Mueller"),
            )
        scrumMasterRepository.save(scrumMaster)

        // When
        val result = scrumMasterRepository.findByProjectId(projectId)

        // Then
        assertThat(result).isNotNull
        assertThat(result!!.teamMemberId).isEqualTo(scrumMaster.teamMemberId)
        assertThat(result.username).isEqualTo(scrumMaster.username)
        assertThat(result.fullName).isEqualTo(scrumMaster.fullName)
    }

    @Test
    fun ensureSaveWorksProperly() {
        // Given
        val scrumMaster =
            ScrumMaster(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = UUID.randomUUID()),
                username = "mmueller",
                fullName = FullName(firstName = "Max", lastName = "Mueller"),
            )

        // When
        scrumMasterRepository.save(scrumMaster)

        // Then
        val savedEntities = scrumMasterEntityRepository.findAll()
        assertThat(savedEntities).hasSize(1)
        val savedScrumMaster = savedEntities.first().toScrumMaster()
        assertThat(savedScrumMaster.id).isNotNull
        assertThat(savedScrumMaster.teamMemberId).isEqualTo(scrumMaster.teamMemberId)
        assertThat(savedScrumMaster.username).isEqualTo(scrumMaster.username)
        assertThat(savedScrumMaster.fullName).isEqualTo(scrumMaster.fullName)
    }
}
