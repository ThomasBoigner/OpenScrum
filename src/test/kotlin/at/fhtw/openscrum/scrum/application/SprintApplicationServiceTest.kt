package at.fhtw.openscrum.scrum.application

import at.fhtw.openscrum.scrum.application.command.InitializeSprintCommand
import at.fhtw.openscrum.scrum.application.dtos.SprintDto
import at.fhtw.openscrum.scrum.application.dtos.SprintStatusDto
import at.fhtw.openscrum.scrum.domain.model.sprint.Sprint
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintId
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintRepository
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class SprintApplicationServiceTest {
    lateinit var sprintApplicationService: SprintApplicationService

    @Mock
    lateinit var sprintService: SprintService

    @Mock
    lateinit var sprintRepository: SprintRepository

    @BeforeEach
    fun setUp() {
        sprintApplicationService = SprintApplicationService(sprintService, sprintRepository)
    }

    @Test
    fun ensureGetSprintsOfProjectWorksProperly() {
        // Given
        val projectId = UUID.randomUUID()
        val sprints =
            listOf(
                Sprint(
                    sprintId = SprintId(projectId = projectId),
                    sprintNumber = 1,
                    startDate = LocalDate.of(2025, 1, 6),
                    sprintLength = 2,
                ),
                Sprint(
                    sprintId = SprintId(projectId = projectId),
                    sprintNumber = 2,
                    startDate = LocalDate.of(2025, 2, 3),
                    sprintLength = 2,
                ),
            )
        whenever(sprintRepository.findSprintsByProjectId(projectId)).thenReturn(sprints)

        // When
        val result = sprintApplicationService.getSprintsOfProject(projectId)

        // Then
        assertThat(result).hasSize(2)
        assertThat(result[0]).isEqualTo(SprintDto(sprints[0]))
        assertThat(result[1]).isEqualTo(SprintDto(sprints[1]))
    }

    @Test
    fun ensureGetSprintsOfProjectReturnsEmptyListWhenNoSprintsExist() {
        // Given
        val projectId = UUID.randomUUID()
        whenever(sprintRepository.findSprintsByProjectId(projectId)).thenReturn(emptyList())

        // When
        val result = sprintApplicationService.getSprintsOfProject(projectId)

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun ensureInitializeSprintWorksProperly() {
        // Given
        val command =
            InitializeSprintCommand(
                projectId = UUID.randomUUID(),
                sprintLength = 2,
            )

        val sprint =
            Sprint(sprintId = SprintId(projectId = command.projectId), sprintNumber = 2, sprintLength = command.sprintLength)

        whenever(sprintService.initializeSprint(command.projectId, command.sprintLength)).thenReturn(sprint)

        // When
        val result = sprintApplicationService.initializeSprint(command)

        // Then
        assertThat(result.projectId).isEqualTo(command.projectId)
        assertThat(result.status).isEqualTo(SprintStatusDto.NOT_PLANNED)
        assertThat(result.endDate).isAfter(result.startDate)
    }
}
