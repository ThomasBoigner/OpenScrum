package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.project

import at.fhtw.openscrum.scrum.domain.model.project.Project
import at.fhtw.openscrum.scrum.domain.model.project.ProjectId
import at.fhtw.openscrum.scrum.domain.model.project.ProjectRepository
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
                sprintLength = 3,
                definitionOfDone = "All tests pass",
                productGoal = "Deliver MVP",
            )

        // When
        projectRepository.save(project)

        // Then
        val savedEntities = projectEntityRepository.findAll()
        assertThat(savedEntities).hasSize(1)
        val savedProject = savedEntities.first().toProject()
        assertThat(savedProject).isEqualTo(project)
        assertThat(savedProject.projectName).isEqualTo(project.projectName)
        assertThat(savedProject.sprintLength).isEqualTo(project.sprintLength)
        assertThat(savedProject.definitionOfDone).isEqualTo(project.definitionOfDone)
        assertThat(savedProject.productGoal).isEqualTo(project.productGoal)
    }
}
