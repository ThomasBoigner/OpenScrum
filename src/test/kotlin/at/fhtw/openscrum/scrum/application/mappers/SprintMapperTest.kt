package at.fhtw.openscrum.scrum.application.mappers

import at.fhtw.openscrum.scrum.application.dtos.SprintDto
import at.fhtw.openscrum.scrum.domain.model.sprint.Sprint
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintId
import at.fhtw.openscrum.scrum.domain.util.Page
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class SprintMapperTest {
    lateinit var sprintMapper: SprintMapper

    @BeforeEach
    fun setUp() {
        sprintMapper = SprintMapper()
    }

    @Test
    fun ensureToSprintDtoPageMapsContent() {
        // Given
        val projectId = UUID.randomUUID()
        val sprint1 = Sprint(sprintId = SprintId(projectId = projectId), sprintNumber = 1, sprintLength = 2)
        val sprint2 = Sprint(sprintId = SprintId(projectId = projectId), sprintNumber = 2, sprintLength = 2)

        val sprintPage =
            Page(
                content = mutableListOf(sprint1, sprint2),
                last = true,
                totalPages = 1,
                totalElements = 2,
                first = true,
                size = 10,
                number = 0,
                numberOfElements = 2,
                empty = false,
            )

        // When
        val result = sprintMapper.toSprintDtoPage(sprintPage)

        // Then
        assertThat(result.content).containsExactly(SprintDto(sprint1), SprintDto(sprint2))
        assertThat(result.last).isEqualTo(sprintPage.last)
        assertThat(result.totalPages).isEqualTo(sprintPage.totalPages)
        assertThat(result.totalElements).isEqualTo(sprintPage.totalElements)
        assertThat(result.first).isEqualTo(sprintPage.first)
        assertThat(result.size).isEqualTo(sprintPage.size)
        assertThat(result.number).isEqualTo(sprintPage.number)
        assertThat(result.numberOfElements).isEqualTo(sprintPage.numberOfElements)
        assertThat(result.empty).isEqualTo(sprintPage.empty)
    }
}
