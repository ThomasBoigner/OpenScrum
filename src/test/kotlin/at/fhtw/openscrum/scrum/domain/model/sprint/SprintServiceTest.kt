package at.fhtw.openscrum.scrum.domain.model.sprint

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class SprintServiceTest {
    lateinit var sprintService: SprintService

    @Mock
    lateinit var sprintRepository: SprintRepository

    @BeforeEach
    fun setUp() {
        sprintService = SprintService(sprintRepository)
    }

    @Test
    fun ensureInitializeSprintWorksProperly() {
        // Given
        val projectId = UUID.randomUUID()
        whenever(sprintRepository.countByProjectId(projectId)).thenReturn(2)
        whenever(sprintRepository.save(any())).thenAnswer { it.arguments[0] }

        // When
        val result = sprintService.initializeSprint(projectId = projectId, sprintLength = 2)

        // Then
        assertThat(result.sprintId.projectId).isEqualTo(projectId)
        assertThat(result.sprintName).isEqualTo("Sprint 3")
        assertThat(result.status).isEqualTo(SprintStatus.NOT_PLANNED)
        assertThat(result.endDate).isAfter(result.startDate)
    }
}
