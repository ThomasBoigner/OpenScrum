package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.sprint

import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItem
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemId
import at.fhtw.openscrum.scrum.domain.model.sprint.Sprint
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintId
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintRepository
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintStatus
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
        assertThat(
            savedItem.sprintBacklogItemId.productBacklogItemId,
        ).isEqualTo(productBacklogItem.productBacklogItemId.productBacklogItemId)
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

    @Test
    fun ensureFindSprintsByEndDateBeforeAndStatusInProgressOrStatusNotPlannedWorksProperly() {
        // Given
        val projectId = UUID.randomUUID()
        val cutoff = LocalDate.of(2025, 1, 2)
        val beforeCutoff = LocalDate.of(2025, 1, 1)
        val afterCutoff = LocalDate.of(2025, 1, 2)

        val inProgressExpired =
            Sprint(
                sprintId = SprintId(projectId = projectId),
                sprintName = "Sprint 1",
                startDate = LocalDate.of(2025, 1, 1),
                endDate = beforeCutoff,
                status = SprintStatus.IN_PROGRESS,
            )
        val notPlannedBeforeCutoff =
            Sprint(
                sprintId = SprintId(projectId = projectId),
                sprintName = "Sprint 2",
                startDate = LocalDate.of(2025, 1, 1),
                endDate = beforeCutoff,
                status = SprintStatus.NOT_PLANNED,
            )
        val notPlannedAfterCutoff =
            Sprint(
                sprintId = SprintId(projectId = projectId),
                sprintName = "Sprint 3",
                startDate = LocalDate.of(2025, 2, 1),
                endDate = afterCutoff,
                status = SprintStatus.NOT_PLANNED,
            )
        val inProgressNotExpired =
            Sprint(
                sprintId = SprintId(projectId = projectId),
                sprintName = "Sprint 4",
                startDate = LocalDate.of(2025, 2, 1),
                endDate = afterCutoff,
                status = SprintStatus.IN_PROGRESS,
            )
        val completed =
            Sprint(
                sprintId = SprintId(projectId = projectId),
                sprintName = "Sprint 5",
                startDate = LocalDate.of(2025, 1, 1),
                endDate = beforeCutoff,
                status = SprintStatus.COMPLETED,
            )
        val cancelled =
            Sprint(
                sprintId = SprintId(projectId = projectId),
                sprintName = "Sprint 6",
                startDate = LocalDate.of(2025, 1, 1),
                endDate = beforeCutoff,
                status = SprintStatus.CANCELLED,
            )

        listOf(inProgressExpired, notPlannedBeforeCutoff, notPlannedAfterCutoff, inProgressNotExpired, completed, cancelled)
            .forEach { sprintRepository.save(it) }

        // When
        val result = sprintRepository.findSprintsByEndDateBeforeAndStatusInProgressOrStatusNotPlanned(cutoff)

        // Then
        assertThat(result).hasSize(3)
        assertThat(result).contains(inProgressExpired, notPlannedBeforeCutoff, notPlannedAfterCutoff)
        assertThat(result).doesNotContain(inProgressNotExpired, completed, cancelled)
    }
}
