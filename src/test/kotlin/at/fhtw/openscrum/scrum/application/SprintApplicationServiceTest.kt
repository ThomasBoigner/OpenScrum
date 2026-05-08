package at.fhtw.openscrum.scrum.application

import at.fhtw.openscrum.scrum.application.command.InitializeSprintCommand
import at.fhtw.openscrum.scrum.application.command.MoveSprintBacklogItemCommand
import at.fhtw.openscrum.scrum.application.command.MoveSprintBacklogItemLeftCommand
import at.fhtw.openscrum.scrum.application.command.PlanSprintCommand
import at.fhtw.openscrum.scrum.application.dtos.SprintBacklogItemStatusDto
import at.fhtw.openscrum.scrum.application.dtos.SprintDto
import at.fhtw.openscrum.scrum.application.dtos.SprintStatusDto
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItem
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemId
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemRepository
import at.fhtw.openscrum.scrum.domain.model.sprint.MoveDirection
import at.fhtw.openscrum.scrum.domain.model.sprint.Sprint
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintBacklogItem
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintBacklogItemId
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintBacklogItemStatus
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintId
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintRepository
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintService
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintStatus
import at.fhtw.openscrum.scrum.domain.model.teammember.Developer
import at.fhtw.openscrum.scrum.domain.model.teammember.DeveloperRepository
import at.fhtw.openscrum.scrum.domain.model.teammember.FullName
import at.fhtw.openscrum.scrum.domain.model.teammember.ScrumMaster
import at.fhtw.openscrum.scrum.domain.model.teammember.ScrumMasterRepository
import at.fhtw.openscrum.scrum.domain.model.teammember.TeamMemberId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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

    @Mock
    lateinit var scrumMasterRepository: ScrumMasterRepository

    @Mock
    lateinit var developerRepository: DeveloperRepository

    @Mock
    lateinit var productBacklogItemRepository: ProductBacklogItemRepository

    @BeforeEach
    fun setUp() {
        sprintApplicationService =
            SprintApplicationService(
                sprintService,
                sprintRepository,
                scrumMasterRepository,
                developerRepository,
                productBacklogItemRepository,
            )
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
    fun ensureGetSprintWorksProperly() {
        // Given
        val projectId = UUID.randomUUID()
        val sprintId = UUID.randomUUID()
        val sprint =
            Sprint(
                sprintId = SprintId(projectId = projectId, sprintId = sprintId),
                sprintNumber = 1,
                startDate = LocalDate.of(2025, 1, 6),
                sprintLength = 2,
            )
        whenever(sprintRepository.findSprintBySprintId(SprintId(projectId, sprintId))).thenReturn(sprint)

        // When
        val result = sprintApplicationService.getSprint(projectId, sprintId)

        // Then
        assertThat(result).isNotNull
        assertThat(result).isEqualTo(SprintDto(sprint))
    }

    @Test
    fun ensureGetSprintReturnsNullWhenSprintDoesNotExist() {
        // Given
        val projectId = UUID.randomUUID()
        val sprintId = UUID.randomUUID()
        whenever(sprintRepository.findSprintBySprintId(SprintId(projectId, sprintId))).thenReturn(null)

        // When
        val result = sprintApplicationService.getSprint(projectId, sprintId)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun ensureGetSprintBacklogItemsWorksProperly() {
        // Given
        val projectId = UUID.randomUUID()
        val sprintId = UUID.randomUUID()
        val toDoItem =
            SprintBacklogItem(
                sprintBacklogItemId =
                    SprintBacklogItemId(
                        projectId = projectId,
                        sprintId = sprintId,
                        productBacklogItemId = UUID.randomUUID(),
                    ),
                title = "Implement login",
                description = "As a user I want to log in",
                status = SprintBacklogItemStatus.TO_DO,
            )
        val doneItem =
            SprintBacklogItem(
                sprintBacklogItemId =
                    SprintBacklogItemId(
                        projectId = projectId,
                        sprintId = sprintId,
                        productBacklogItemId = UUID.randomUUID(),
                    ),
                title = "Implement logout",
                description = "As a user I want to log out",
                status = SprintBacklogItemStatus.DONE,
            )
        val sprint =
            Sprint(
                sprintId = SprintId(projectId = projectId, sprintId = sprintId),
                sprintName = "Sprint 1",
                startDate = LocalDate.of(2025, 1, 6),
                endDate = LocalDate.of(2025, 1, 19),
                sprintBacklogItems = mutableSetOf(toDoItem, doneItem),
            )
        whenever(sprintRepository.findSprintBySprintId(SprintId(projectId, sprintId))).thenReturn(sprint)

        // When
        val result = sprintApplicationService.getSprintBacklogItems(projectId, sprintId, SprintBacklogItemStatus.TO_DO)

        // Then
        assertThat(result).hasSize(1)
        assertThat(result[0].productBacklogItemId).isEqualTo(toDoItem.sprintBacklogItemId.productBacklogItemId)
        assertThat(result[0].title).isEqualTo(toDoItem.title)
        assertThat(result[0].status).isEqualTo(SprintBacklogItemStatusDto.TO_DO)
    }

    @Test
    fun ensureGetSprintBacklogItemsPopulatesAssignedDeveloper() {
        // Given
        val projectId = UUID.randomUUID()
        val sprintId = UUID.randomUUID()
        val developerTeamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId)
        val inProgressItem =
            SprintBacklogItem(
                sprintBacklogItemId =
                    SprintBacklogItemId(
                        projectId = projectId,
                        sprintId = sprintId,
                        productBacklogItemId = UUID.randomUUID(),
                    ),
                title = "Implement registration",
                description = "As a user I want to register",
                assignedDeveloper = developerTeamMemberId,
                status = SprintBacklogItemStatus.IN_PROGRESS,
            )
        val sprint =
            Sprint(
                sprintId = SprintId(projectId = projectId, sprintId = sprintId),
                sprintName = "Sprint 1",
                startDate = LocalDate.of(2025, 1, 6),
                endDate = LocalDate.of(2025, 1, 19),
                sprintBacklogItems = mutableSetOf(inProgressItem),
            )
        val developer =
            Developer(
                teamMemberId = developerTeamMemberId,
                username = "jane.doe",
                fullName = FullName(firstName = "Jane", lastName = "Doe"),
            )
        whenever(sprintRepository.findSprintBySprintId(SprintId(projectId, sprintId))).thenReturn(sprint)
        whenever(developerRepository.findByTeamMemberId(developerTeamMemberId)).thenReturn(developer)

        // When
        val result =
            sprintApplicationService.getSprintBacklogItems(projectId, sprintId, SprintBacklogItemStatus.IN_PROGRESS)

        // Then
        assertThat(result).hasSize(1)
        assertThat(result[0].assignedDeveloper).isNotNull
        assertThat(result[0].assignedDeveloper!!.userId).isEqualTo(developerTeamMemberId.userId)
    }

    @Test
    fun ensureGetSprintBacklogItemsThrowsExceptionWhenSprintCanNotBeFound() {
        // Given
        val projectId = UUID.randomUUID()
        val sprintId = UUID.randomUUID()
        whenever(sprintRepository.findSprintBySprintId(SprintId(projectId, sprintId))).thenReturn(null)

        // When
        assertThrows<IllegalArgumentException> {
            sprintApplicationService.getSprintBacklogItems(projectId, sprintId, SprintBacklogItemStatus.TO_DO)
        }
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
            Sprint(
                sprintId = SprintId(projectId = command.projectId),
                sprintNumber = 2,
                sprintLength = command.sprintLength,
            )

        whenever(sprintService.initializeSprint(command.projectId, command.sprintLength)).thenReturn(sprint)

        // When
        val result = sprintApplicationService.initializeSprint(command)

        // Then
        assertThat(result.projectId).isEqualTo(command.projectId)
        assertThat(result.status).isEqualTo(SprintStatusDto.NOT_PLANNED)
        assertThat(result.endDate).isAfter(result.startDate)
    }

    @Test
    fun ensurePlanSprintWorksProperly() {
        // Given
        val projectId = UUID.randomUUID()
        val sprintId = UUID.randomUUID()
        val pbi1Id = UUID.randomUUID()
        val pbi2Id = UUID.randomUUID()
        val username = "john.doe"
        val command =
            PlanSprintCommand(
                sprintGoal = "Deliver user authentication feature",
                projectId = projectId,
                sprintId = sprintId,
                productBacklogIds = setOf(pbi1Id, pbi2Id),
            )
        val sprint =
            Sprint(
                sprintId = SprintId(projectId = projectId, sprintId = sprintId),
                sprintNumber = 1,
                startDate = LocalDate.of(2025, 1, 6),
                sprintLength = 2,
            )
        val scrumMaster =
            ScrumMaster(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
                username = username,
                fullName = FullName(firstName = "John", lastName = "Doe"),
            )
        val pbi1 =
            ProductBacklogItem(
                productBacklogItemId = ProductBacklogItemId(projectId = projectId, productBacklogItemId = pbi1Id),
                title = "Implement login",
                description = "As a user I want to log in",
            )
        val pbi2 =
            ProductBacklogItem(
                productBacklogItemId = ProductBacklogItemId(projectId = projectId, productBacklogItemId = pbi2Id),
                title = "Implement registration",
                description = "As a user I want to register",
            )
        whenever(sprintRepository.findSprintBySprintId(SprintId(projectId, sprintId))).thenReturn(sprint)
        whenever(scrumMasterRepository.findByProjectIdAndUsername(projectId, username)).thenReturn(scrumMaster)
        whenever(
            productBacklogItemRepository.findProductBacklogItemByProductBacklogItemId(
                ProductBacklogItemId(projectId = projectId, productBacklogItemId = pbi1Id),
            ),
        ).thenReturn(pbi1)
        whenever(
            productBacklogItemRepository.findProductBacklogItemByProductBacklogItemId(
                ProductBacklogItemId(projectId = projectId, productBacklogItemId = pbi2Id),
            ),
        ).thenReturn(pbi2)
        whenever(sprintRepository.save(any())).thenAnswer { it.arguments[0] }

        // When
        val result = sprintApplicationService.planSprint(username, command)

        // Then
        assertThat(result.sprintGoal).isEqualTo(command.sprintGoal)
        assertThat(result.status).isEqualTo(SprintStatusDto.IN_PROGRESS)
        assertThat(result.projectId).isEqualTo(projectId)
        assertThat(result.sprintId).isEqualTo(sprintId)
    }

    @Test
    fun ensurePlanSprintThrowsExceptionWhenSprintCanNotBeFound() {
        // Given
        val projectId = UUID.randomUUID()
        val sprintId = UUID.randomUUID()
        val command =
            PlanSprintCommand(
                sprintGoal = "Deliver user authentication feature",
                projectId = projectId,
                sprintId = sprintId,
                productBacklogIds = setOf(UUID.randomUUID()),
            )
        whenever(sprintRepository.findSprintBySprintId(SprintId(projectId, sprintId))).thenReturn(null)

        // When
        assertThrows<IllegalArgumentException> {
            sprintApplicationService.planSprint("john.doe", command)
        }
    }

    @Test
    fun ensurePlanSprintThrowsExceptionWhenProductBacklogItemCanNotBeFound() {
        // Given
        val projectId = UUID.randomUUID()
        val sprintId = UUID.randomUUID()
        val missingPbiId = UUID.randomUUID()
        val username = "john.doe"
        val command =
            PlanSprintCommand(
                sprintGoal = "Deliver user authentication feature",
                projectId = projectId,
                sprintId = sprintId,
                productBacklogIds = setOf(missingPbiId),
            )
        val sprint =
            Sprint(
                sprintId = SprintId(projectId = projectId, sprintId = sprintId),
                sprintNumber = 1,
                startDate = LocalDate.of(2025, 1, 6),
                sprintLength = 2,
            )

        whenever(sprintRepository.findSprintBySprintId(SprintId(projectId, sprintId))).thenReturn(sprint)
        whenever(
            productBacklogItemRepository.findProductBacklogItemByProductBacklogItemId(
                ProductBacklogItemId(projectId = projectId, productBacklogItemId = missingPbiId),
            ),
        ).thenReturn(null)

        // When
        assertThrows<IllegalArgumentException> {
            sprintApplicationService.planSprint(username, command)
        }
    }

    @Test
    fun ensureMoveSprintBacklogItemWorksProperly() {
        // Given
        val projectId = UUID.randomUUID()
        val sprintId = UUID.randomUUID()
        val productBacklogItemId = UUID.randomUUID()
        val username = "john.doe"
        val command =
            MoveSprintBacklogItemCommand(
                projectId = projectId,
                sprintId = sprintId,
                productBacklogItemId = productBacklogItemId,
                moveDirection = MoveDirection.RIGHT,
            )
        val sprintBacklogItem =
            SprintBacklogItem(
                sprintBacklogItemId =
                    SprintBacklogItemId(
                        projectId = projectId,
                        sprintId = sprintId,
                        productBacklogItemId = productBacklogItemId,
                    ),
                title = "Implement login",
                description = "As a user I want to log in",
                status = SprintBacklogItemStatus.TO_DO,
            )
        val sprint =
            Sprint(
                sprintId = SprintId(projectId = projectId, sprintId = sprintId),
                sprintName = "Sprint 1",
                startDate = LocalDate.of(2025, 1, 6),
                endDate = LocalDate.of(2025, 1, 19),
                sprintBacklogItems = mutableSetOf(sprintBacklogItem),
                status = SprintStatus.IN_PROGRESS
            )
        val developer =
            Developer(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
                username = username,
                fullName = FullName(firstName = "John", lastName = "Doe"),
            )
        whenever(sprintRepository.findSprintBySprintId(SprintId(projectId, sprintId))).thenReturn(sprint)
        whenever(developerRepository.findByProjectIdAndUsername(projectId, username)).thenReturn(developer)
        whenever(sprintRepository.save(any())).thenAnswer { it.arguments[0] }

        // When
        val result = sprintApplicationService.moveSprintBacklogItem(username, command)

        // Then
        assertThat(result.projectId).isEqualTo(projectId)
        assertThat(result.sprintId).isEqualTo(sprintId)
        assertThat(sprintBacklogItem.status).isEqualTo(SprintBacklogItemStatus.IN_PROGRESS)
        assertThat(sprintBacklogItem.assignedDeveloper).isEqualTo(developer.teamMemberId)
    }

    @Test
    fun ensureMoveSprintBacklogItemRightThrowsExceptionWhenSprintCanNotBeFound() {
        // Given
        val projectId = UUID.randomUUID()
        val sprintId = UUID.randomUUID()
        val command =
            MoveSprintBacklogItemCommand(
                projectId = projectId,
                sprintId = sprintId,
                productBacklogItemId = UUID.randomUUID(),
                moveDirection = MoveDirection.RIGHT,
            )
        whenever(sprintRepository.findSprintBySprintId(SprintId(projectId, sprintId))).thenReturn(null)

        // When
        assertThrows<IllegalArgumentException> {
            sprintApplicationService.moveSprintBacklogItem("john.doe", command)
        }
    }
}
