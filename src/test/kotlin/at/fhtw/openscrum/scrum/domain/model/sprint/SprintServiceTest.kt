package at.fhtw.openscrum.scrum.domain.model.sprint

import at.fhtw.openscrum.scrum.domain.util.Page
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
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
    fun ensureGetSprintsWorksProperly() {
        // Given
        val projectId = UUID.randomUUID()
        val sprint =
            Sprint(
                sprintId = SprintId(projectId = projectId),
                sprintNumber = 6,
                sprintLength = 2,
            )
        val sprintPage =
            Page(
                content = mutableListOf(sprint),
                last = true,
                totalPages = 2,
                totalElements = 6,
                first = false,
                size = 5,
                number = 1,
                numberOfElements = 1,
                empty = false,
            )

        whenever(sprintRepository.findSprintsByProjectId(projectId, "sprint", 1, 5))
            .thenReturn(sprintPage)

        // When
        val result = sprintService.getSprints(projectId, "sprint", 1, 5)

        // Then
        assertThat(result).isEqualTo(sprintPage)
    }

    @Test
    fun ensureGetSprintsThrowsExceptionForNegativePage() {
        // When
        val exception =
            assertThrows<IllegalArgumentException> {
                sprintService.getSprints(UUID.randomUUID(), page = -1)
            }

        // Then
        assertThat(exception.message).isEqualTo("Page number must not be negative!")
        verify(sprintRepository, never())
            .findSprintsByProjectId(any(), anyOrNull(), any(), any(), any())
    }

    @Test
    fun ensureGetSprintsThrowsExceptionForNonPositiveSize() {
        // When
        val exception =
            assertThrows<IllegalArgumentException> {
                sprintService.getSprints(UUID.randomUUID(), size = 0)
            }

        // Then
        assertThat(exception.message).isEqualTo("Page size must be positive!")
        verify(sprintRepository, never())
            .findSprintsByProjectId(any(), anyOrNull(), any(), any(), any())
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
