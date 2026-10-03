package at.fhtw.openscrum.management.infrastructure.persistence.jpa.project

import at.fhtw.openscrum.management.domain.model.project.ProjectId
import at.fhtw.openscrum.management.domain.model.user.UserId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.util.UUID

class ProjectEntityMapperTest {
    lateinit var projectEntityMapper: ProjectEntityMapper

    @BeforeEach
    fun setUp() {
        projectEntityMapper = ProjectEntityMapper()
    }

    @Test
    fun ensureToProjectPageMapsContent() {
        // Given
        val project1 =
            ProjectEntity(
                id = 42,
                projectId = UUID.randomUUID(),
                projectName = "OpenScrum",
                productOwnerId = UUID.randomUUID(),
                scrumMasterId = UUID.randomUUID(),
                developerIds = setOf(UUID.randomUUID()),
            )

        val project2 =
            ProjectEntity(
                id = 43,
                projectId = UUID.randomUUID(),
                projectName = "OpenKanban",
                productOwnerId = UUID.randomUUID(),
                scrumMasterId = UUID.randomUUID(),
                developerIds = setOf(UUID.randomUUID()),
            )

        val projectEntities =
            mutableListOf(
                project1,
                project2,
            )

        val entityPage = PageImpl(projectEntities, PageRequest.of(0, 10), 1)

        // When
        val result = projectEntityMapper.toProjectPage(entityPage)

        // Then
        assertThat(result.content).hasSize(2)
        assertThat(result.content).containsExactlyInAnyOrder(project1.toProject(), project2.toProject())
    }
}
