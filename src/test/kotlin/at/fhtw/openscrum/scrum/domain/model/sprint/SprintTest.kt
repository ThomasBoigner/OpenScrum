package at.fhtw.openscrum.scrum.domain.model.sprint

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.UUID

class SprintTest {
    @Test
    fun ensureSprintIsCreated() {
        // Given
        val projectId = UUID.randomUUID()
        val startDate = LocalDate.of(2000, 1, 1)
        val sprintLength = 2L

        // When
        val sprint =
            Sprint(sprintId = SprintId(projectId = projectId), sprintNumber = 1, startDate = startDate, sprintLength = sprintLength)

        // Then
        assertThat(sprint.sprintId.projectId).isEqualTo(projectId)
        assertThat(sprint.sprintName).isEqualTo("Sprint 1")
        assertThat(sprint.startDate).isEqualTo(startDate)
        assertThat(sprint.endDate).isEqualTo(LocalDate.of(2000, 1, 17))
    }

    @Test
    fun ensureSprintLengthIsCalculatedCorrectlyOnAMonday() {
        // Given
        val projectId = UUID.randomUUID()
        val startDate = LocalDate.of(2000, 1, 3)
        val sprintLength = 2L

        // When
        val sprint =
            Sprint(sprintId = SprintId(projectId = projectId), sprintNumber = 2, startDate = startDate, sprintLength = sprintLength)

        // Then
        assertThat(sprint.sprintId.projectId).isEqualTo(projectId)
        assertThat(sprint.sprintName).isEqualTo("Sprint 2")
        assertThat(sprint.startDate).isEqualTo(startDate)
        assertThat(sprint.endDate).isEqualTo(LocalDate.of(2000, 1, 17))
    }
}
