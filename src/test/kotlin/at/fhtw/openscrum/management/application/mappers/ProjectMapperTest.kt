package at.fhtw.openscrum.management.application.mappers

import at.fhtw.openscrum.management.application.dtos.ProjectDto
import at.fhtw.openscrum.management.domain.model.project.Project
import at.fhtw.openscrum.management.domain.model.user.UserId
import at.fhtw.openscrum.management.domain.util.Page
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ProjectMapperTest {
    lateinit var projectMapper: ProjectMapper

    @BeforeEach
    fun setUp() {
        projectMapper = ProjectMapper()
    }

    @Test
    fun ensureToProjectDtoPageMapsContent() {
        // Given
        val project1 =
            Project(
                projectName = "OpenScrum",
                productOwnerId = UserId(),
                scrumMasterId = UserId(),
                developerIds = setOf(UserId()),
            )

        val project2 =
            Project(
                projectName = "OpenKanban",
                productOwnerId = UserId(),
                scrumMasterId = UserId(),
                developerIds = setOf(UserId(), UserId()),
            )

        val projectPage =
            Page(
                content = mutableListOf(project1, project2),
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
        val result = projectMapper.toProjectDtoPage(projectPage)

        // Then
        assertThat(result.content).hasSize(2)
        assertThat(result.content).containsExactly(
            ProjectDto(project1),
            ProjectDto(project2),
        )
        assertThat(result.last).isEqualTo(projectPage.last)
        assertThat(result.totalPages).isEqualTo(projectPage.totalPages)
        assertThat(result.totalElements).isEqualTo(projectPage.totalElements)
        assertThat(result.first).isEqualTo(projectPage.first)
        assertThat(result.size).isEqualTo(projectPage.size)
        assertThat(result.number).isEqualTo(projectPage.number)
        assertThat(result.numberOfElements).isEqualTo(projectPage.numberOfElements)
        assertThat(result.empty).isEqualTo(projectPage.empty)
    }
}
