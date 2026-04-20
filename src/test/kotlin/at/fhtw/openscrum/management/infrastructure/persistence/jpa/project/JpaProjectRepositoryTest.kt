package at.fhtw.openscrum.management.infrastructure.persistence.jpa.project

import at.fhtw.openscrum.management.domain.model.project.Project
import at.fhtw.openscrum.management.domain.model.project.ProjectRepository
import at.fhtw.openscrum.management.domain.model.user.UserId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

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
                projectName = "OpenScrum",
                productOwnerId = UserId(),
                scrumMasterId = UserId(),
                developerIds = setOf(UserId()),
            )

        // When
        projectRepository.save(project)

        // Then
        val savedProjects = projectRepository.findAll()
        assertThat(savedProjects).hasSize(1)
        assertThat(savedProjects.first()).isEqualTo(project)
    }

    @Test
    fun ensureExistsByProjectNameWorksProperly() {
        // Given
        val project =
            Project(
                projectName = "OpenScrum",
                productOwnerId = UserId(),
                scrumMasterId = UserId(),
            )
        projectRepository.save(project)

        // When
        val result = projectRepository.existsByProjectName(project.projectName)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun ensureFindAllWorksProperly() {
        // Given
        val project1 =
            Project(
                projectName = "OpenScrum",
                productOwnerId = UserId(),
                scrumMasterId = UserId(),
            )
        val project2 =
            Project(
                projectName = "AnotherProject",
                productOwnerId = UserId(),
                scrumMasterId = UserId(),
            )
        projectRepository.save(project1)
        projectRepository.save(project2)

        // When
        val result = projectRepository.findAll()

        // Then
        assertThat(result).hasSize(2)
        assertThat(result).containsExactlyInAnyOrder(project1, project2)
    }
}
