package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.sprint

import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItem
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemId
import at.fhtw.openscrum.scrum.domain.model.sprint.Sprint
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintId
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintRepository
import at.fhtw.openscrum.scrum.domain.model.teammember.FullName
import at.fhtw.openscrum.scrum.domain.model.teammember.ScrumMaster
import at.fhtw.openscrum.scrum.domain.model.teammember.TeamMemberId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.util.UUID

@SpringBootTest
@ActiveProfiles("postgres")
class JpaSprintRepositoryTest {
    @Autowired
    lateinit var sprintRepository: SprintRepository

    @Autowired
    lateinit var sprintEntityRepository: SprintEntityRepository

    @BeforeEach
    fun cleanUp() {
        sprintEntityRepository.deleteAll()
    }

    @Test
    fun ensureSaveWorksProperly() {
        // Given
        val projectId = UUID.randomUUID()
        val productBacklogItem =
            ProductBacklogItem(
                productBacklogItemId = ProductBacklogItemId(projectId = projectId),
                title = "Implement login",
                description = "As a user I want to log in",
            )
        val sprint =
            Sprint(
                sprintId = SprintId(projectId = projectId),
                sprintNumber = 1,
                startDate = LocalDate.of(2025, 1, 6),
                sprintLength = 2,
            )
        val scrumMaster =
            ScrumMaster(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
                username = "john.doe",
                fullName = FullName(firstName = "John", lastName = "Doe"),
            )
        sprint.planSprint(scrumMaster, "Sprint Goal", setOf(productBacklogItem))

        // When
        sprintRepository.save(sprint)

        // Then
        val savedEntities = sprintEntityRepository.findAll()
        assertThat(savedEntities).hasSize(1)
        val savedSprint = savedEntities.first().toSprint()
        assertThat(savedSprint).isEqualTo(sprint)
        assertThat(savedSprint.sprintName).isEqualTo("Sprint 1")
        assertThat(savedSprint.sprintBacklogItems).hasSize(1)
        val savedItem = savedSprint.sprintBacklogItems.first()
        assertThat(savedItem.title).isEqualTo(productBacklogItem.title)
        assertThat(savedItem.description).isEqualTo(productBacklogItem.description)
        assertThat(savedItem.productBacklogItemId).isEqualTo(productBacklogItem.productBacklogItemId)
    }

    @Test
    fun ensureFindSprintBySprintIdWorksProperly() {
        // Given
        val sprintId = SprintId(projectId = UUID.randomUUID())
        val sprint =
            Sprint(
                sprintId = sprintId,
                sprintNumber = 1,
                startDate = LocalDate.of(2025, 1, 6),
                sprintLength = 2,
            )
        sprintRepository.save(sprint)

        // When
        val result = sprintRepository.findSprintBySprintId(sprintId)

        // Then
        assertThat(result).isNotNull
        assertThat(result).isEqualTo(sprint)
        assertThat(result!!.sprintName).isEqualTo("Sprint 1")
    }
}
