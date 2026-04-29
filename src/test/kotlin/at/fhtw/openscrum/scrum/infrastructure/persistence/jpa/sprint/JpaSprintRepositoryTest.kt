package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.sprint

import at.fhtw.openscrum.scrum.domain.model.sprint.Sprint
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintId
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintRepository
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
        val sprint =
            Sprint(
                sprintId = SprintId(projectId = UUID.randomUUID()),
                startDate = LocalDate.of(2025, 1, 6),
                sprintLength = 2,
            )

        // When
        sprintRepository.save(sprint)

        // Then
        val savedEntities = sprintEntityRepository.findAll()
        assertThat(savedEntities).hasSize(1)
        val savedSprint = savedEntities.first().toSprint()
        assertThat(savedSprint).isEqualTo(sprint)
    }
}
