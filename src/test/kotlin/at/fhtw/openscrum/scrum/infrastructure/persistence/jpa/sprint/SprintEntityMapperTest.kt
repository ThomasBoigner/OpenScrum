package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.sprint

import at.fhtw.openscrum.scrum.domain.model.sprint.Sprint
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.util.UUID

class SprintEntityMapperTest {
    lateinit var sprintEntityMapper: SprintEntityMapper

    @BeforeEach
    fun setUp() {
        sprintEntityMapper = SprintEntityMapper()
    }

    @Test
    fun ensureToSprintPageWorksProperly() {
        // Given
        val projectId = UUID.randomUUID()
        val sprint1 = SprintEntity(Sprint(sprintId = SprintId(projectId = projectId), sprintNumber = 1, sprintLength = 2))
        val sprint2 = SprintEntity(Sprint(sprintId = SprintId(projectId = projectId), sprintNumber = 2, sprintLength = 2))

        val entityPage = PageImpl(mutableListOf(sprint1, sprint2), PageRequest.of(0, 10), 2)

        // When
        val result = sprintEntityMapper.toSprintPage(entityPage)

        // Then
        assertThat(result.content).containsExactly(sprint1.toSprint(), sprint2.toSprint())
        assertThat(result.first).isTrue()
        assertThat(result.last).isTrue()
        assertThat(result.totalElements).isEqualTo(2)
    }
}
