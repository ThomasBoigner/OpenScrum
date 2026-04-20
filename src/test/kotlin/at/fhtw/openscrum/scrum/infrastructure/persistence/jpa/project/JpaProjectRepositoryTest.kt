package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.project

import at.fhtw.openscrum.scrum.domain.model.project.DefinitionOfDone
import at.fhtw.openscrum.scrum.domain.model.project.Project
import at.fhtw.openscrum.scrum.domain.model.project.ProjectId
import at.fhtw.openscrum.scrum.domain.model.project.ProjectRepository
import at.fhtw.openscrum.scrum.domain.model.project.SprintLength
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
class JpaProjectRepositoryTest {
    @Autowired
    lateinit var projectRepository: ProjectRepository

    @Autowired
    lateinit var projectEntityRepository: ProjectEntityRepository

    @BeforeEach
    fun cleanUp() {
        projectEntityRepository.deleteAll()
    }

    @Test
    fun ensureSaveWorksProperly() {
        // Given
        val project =
            Project(
                projectId = ProjectId(UUID.randomUUID()),
                projectName = "OpenScrum",
                sprintLength = SprintLength(3),
                definitionOfDone = DefinitionOfDone("All tests pass"),
                productGoal = "Deliver MVP",
            )

        // When
        projectRepository.save(project)

        // Then
        val savedEntities = projectEntityRepository.findAll()
        assertThat(savedEntities).hasSize(1)
        val savedProject = savedEntities.first().toProject()
        assertThat(savedProject).isEqualTo(project)
    }

    @Test
    fun ensureFindByProjectIdWorksProperly() {
        // Given
        val project =
            Project(
                projectId = ProjectId(UUID.randomUUID()),
                projectName = "OpenScrum",
                sprintLength = SprintLength(3),
                definitionOfDone = DefinitionOfDone("All tests pass"),
                productGoal = "Deliver MVP",
            )
        projectRepository.save(project)

        // When
        val result = projectRepository.findByProjectId(project.projectId)

        // Then
        assertThat(result).isNotNull
        assertThat(result).isEqualTo(project)
    }
}
