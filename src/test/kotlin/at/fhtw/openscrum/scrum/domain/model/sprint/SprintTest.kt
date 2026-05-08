package at.fhtw.openscrum.scrum.domain.model.sprint

import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItem
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemId
import at.fhtw.openscrum.scrum.domain.model.teammember.Developer
import at.fhtw.openscrum.scrum.domain.model.teammember.FullName
import at.fhtw.openscrum.scrum.domain.model.teammember.ScrumMaster
import at.fhtw.openscrum.scrum.domain.model.teammember.TeamMemberId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.util.UUID

class SprintTest {
    @Test
    fun ensureSprintIsCreated() {
        // Given
        val projectId = UUID.randomUUID()
        val startDate = LocalDate.of(2000, 1, 1)
        val sprintLength = 2L

        // When
        val sprint =
            Sprint(
                sprintId = SprintId(projectId = projectId),
                sprintNumber = 1,
                startDate = startDate,
                sprintLength = sprintLength,
            )

        // Then
        assertThat(sprint.sprintId.projectId).isEqualTo(projectId)
        assertThat(sprint.sprintName).isEqualTo("Sprint 1")
        assertThat(sprint.startDate).isEqualTo(startDate)
        assertThat(sprint.endDate).isEqualTo(LocalDate.of(2000, 1, 16))
    }

    @Test
    fun ensureSprintLengthIsCalculatedCorrectlyOnAMonday() {
        // Given
        val projectId = UUID.randomUUID()
        val startDate = LocalDate.of(2000, 1, 3)
        val sprintLength = 2L

        // When
        val sprint =
            Sprint(
                sprintId = SprintId(projectId = projectId),
                sprintNumber = 2,
                startDate = startDate,
                sprintLength = sprintLength,
            )

        // Then
        assertThat(sprint.sprintId.projectId).isEqualTo(projectId)
        assertThat(sprint.sprintName).isEqualTo("Sprint 2")
        assertThat(sprint.startDate).isEqualTo(startDate)
        assertThat(sprint.endDate).isEqualTo(LocalDate.of(2000, 1, 16))
    }

    @Test
    fun ensureSprintIsPlannedWithGoalAndCommittedBacklogItems() {
        // Given
        val projectId = UUID.randomUUID()
        val scrumMaster =
            ScrumMaster(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
                username = "john.doe",
                fullName = FullName(firstName = "John", lastName = "Doe"),
            )
        val sprint =
            Sprint(
                sprintId = SprintId(projectId = projectId),
                sprintNumber = 1,
                startDate = LocalDate.of(2000, 1, 1),
                sprintLength = 2L,
            )
        val sprintGoal = "Deliver user authentication feature"
        val pbi1 =
            ProductBacklogItem(
                productBacklogItemId = ProductBacklogItemId(projectId = projectId),
                title = "Implement login",
                description = "As a user I want to log in",
            )
        val pbi2 =
            ProductBacklogItem(
                productBacklogItemId = ProductBacklogItemId(projectId = projectId),
                title = "Implement registration",
                description = "As a user I want to register",
            )
        val productBacklogItems = setOf(pbi1, pbi2)

        // When
        sprint.planSprint(scrumMaster, sprintGoal, productBacklogItems)

        // Then
        assertThat(sprint.sprintGoal).isEqualTo(sprintGoal)
        assertThat(sprint.sprintBacklogItems).hasSize(2)
        assertThat(sprint.status).isEqualTo(SprintStatus.IN_PROGRESS)
        assertThat(sprint.sprintBacklogItems.map { it.sprintBacklogItemId.productBacklogItemId })
            .contains(pbi1.productBacklogItemId.productBacklogItemId, pbi2.productBacklogItemId.productBacklogItemId)
        assertThat(sprint.sprintBacklogItems.map { it.title })
            .contains(pbi1.title, pbi2.title)
        assertThat(sprint.sprintBacklogItems.map { it.description })
            .contains(pbi1.description, pbi2.description)
        assertThat(sprint.sprintBacklogItems.map { it.productBacklogItemCommittedEvents }).hasSize(2)
    }

    @Test
    fun ensureSprintPlanningFailsWhenScrumMasterBelongsToDifferentProject() {
        // Given
        val projectId = UUID.randomUUID()
        val otherProjectId = UUID.randomUUID()
        val scrumMasterOfAnotherProject =
            ScrumMaster(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = otherProjectId),
                username = "jane.doe",
                fullName = FullName(firstName = "Jane", lastName = "Doe"),
            )
        val sprint =
            Sprint(
                sprintId = SprintId(projectId = projectId),
                sprintNumber = 1,
                startDate = LocalDate.of(2000, 1, 1),
                sprintLength = 2L,
            )
        val sprintGoal = "Deliver user authentication feature"
        val productBacklogItems =
            setOf(
                ProductBacklogItem(
                    productBacklogItemId = ProductBacklogItemId(projectId = projectId),
                    title = "Implement login",
                    description = "As a user I want to log in",
                ),
            )

        // When
        assertThrows<IllegalArgumentException> {
            sprint.planSprint(
                scrumMasterOfAnotherProject,
                sprintGoal,
                productBacklogItems,
            )
        }
    }

    @Test
    fun ensureSprintPlanningFailsWhenCallerIsNotAScrumMaster() {
        // Given
        val projectId = UUID.randomUUID()

        val sprint =
            Sprint(
                sprintId = SprintId(projectId = projectId),
                sprintNumber = 1,
                startDate = LocalDate.of(2000, 1, 1),
                sprintLength = 2L,
            )
        val sprintGoal = "Deliver user authentication feature"
        val productBacklogItems =
            setOf(
                ProductBacklogItem(
                    productBacklogItemId = ProductBacklogItemId(projectId = projectId),
                    title = "Implement login",
                    description = "As a user I want to log in",
                ),
            )

        // When
        assertThrows<IllegalArgumentException> {
            sprint.planSprint(null, sprintGoal, productBacklogItems)
        }
    }

    @Test
    fun ensureSprintPlanningFailsWhenSprintIsAlreadyPlanned() {
        // Given
        val projectId = UUID.randomUUID()
        val scrumMaster =
            ScrumMaster(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
                username = "john.doe",
                fullName = FullName(firstName = "John", lastName = "Doe"),
            )
        val sprint =
            Sprint(
                sprintId = SprintId(projectId = projectId),
                sprintName = "Sprint 1",
                startDate = LocalDate.of(2000, 1, 1),
                endDate = LocalDate.of(2000, 1, 14),
                status = SprintStatus.IN_PROGRESS,
            )
        val sprintGoal = "Deliver user authentication feature"
        val productBacklogItems =
            setOf(
                ProductBacklogItem(
                    productBacklogItemId = ProductBacklogItemId(projectId = projectId),
                    title = "Implement login",
                    description = "As a user I want to log in",
                ),
            )

        // When
        assertThrows<IllegalArgumentException> {
            sprint.planSprint(scrumMaster, sprintGoal, productBacklogItems)
        }
    }

    @Test
    fun ensureSprintPlanningFailsWhenProductBacklogItemsBelongToAnotherProject() {
        // Given
        val projectId = UUID.randomUUID()
        val otherProjectId = UUID.randomUUID()
        val scrumMaster =
            ScrumMaster(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
                username = "john.doe",
                fullName = FullName(firstName = "John", lastName = "Doe"),
            )
        val sprint =
            Sprint(
                sprintId = SprintId(projectId = projectId),
                sprintNumber = 1,
                startDate = LocalDate.of(2000, 1, 1),
                sprintLength = 2L,
            )
        val sprintGoal = "Deliver user authentication feature"
        val productBacklogItems =
            setOf(
                ProductBacklogItem(
                    productBacklogItemId = ProductBacklogItemId(projectId = otherProjectId),
                    title = "Implement login",
                    description = "As a user I want to log in",
                ),
            )

        // When
        assertThrows<IllegalArgumentException> {
            sprint.planSprint(scrumMaster, sprintGoal, productBacklogItems)
        }
    }

    @Test
    fun ensureSprintPlanningFailsWhenSprintGoalIsBlank() {
        // Given
        val projectId = UUID.randomUUID()
        val scrumMaster =
            ScrumMaster(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
                username = "john.doe",
                fullName = FullName(firstName = "John", lastName = "Doe"),
            )
        val sprint =
            Sprint(
                sprintId = SprintId(projectId = projectId),
                sprintNumber = 1,
                startDate = LocalDate.of(2000, 1, 1),
                sprintLength = 2L,
            )
        val blankSprintGoal = "   "
        val productBacklogItems =
            setOf(
                ProductBacklogItem(
                    productBacklogItemId = ProductBacklogItemId(projectId = projectId),
                    title = "Implement login",
                    description = "As a user I want to log in",
                ),
            )

        // When
        assertThrows<IllegalArgumentException> {
            sprint.planSprint(scrumMaster, blankSprintGoal, productBacklogItems)
        }
    }

    @Test
    fun ensureSprintPlanningFailsWhenNoProductBacklogItemsAreCommitted() {
        // Given
        val projectId = UUID.randomUUID()
        val scrumMaster =
            ScrumMaster(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
                username = "john.doe",
                fullName = FullName(firstName = "John", lastName = "Doe"),
            )
        val sprint =
            Sprint(
                sprintId = SprintId(projectId = projectId),
                sprintNumber = 1,
                startDate = LocalDate.of(2000, 1, 1),
                sprintLength = 2L,
            )
        val sprintGoal = "Deliver user authentication feature"

        // When
        assertThrows<IllegalArgumentException> {
            sprint.planSprint(scrumMaster, sprintGoal, setOf())
        }
    }

    @Test
    fun ensureGetSprintBacklogItemsReturnsOnlyItemsWithMatchingStatus() {
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
                status = SprintBacklogItemStatus.IN_PROGRESS,
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
                startDate = LocalDate.of(2000, 1, 1),
                endDate = LocalDate.of(2000, 1, 14),
                sprintBacklogItems = mutableSetOf(toDoItem, inProgressItem, doneItem),
            )

        // When
        val toDoItems = sprint.getSprintBacklogItems(SprintBacklogItemStatus.TO_DO)
        val inProgressItems = sprint.getSprintBacklogItems(SprintBacklogItemStatus.IN_PROGRESS)
        val doneItems = sprint.getSprintBacklogItems(SprintBacklogItemStatus.DONE)

        // Then
        assertThat(toDoItems).containsExactly(toDoItem)
        assertThat(inProgressItems).containsExactly(inProgressItem)
        assertThat(doneItems).containsExactly(doneItem)
    }

    @Test
    fun ensureGetSprintBacklogItemsReturnsEmptyListWhenNoItemsMatchStatus() {
        // Given
        val projectId = UUID.randomUUID()
        val sprint =
            Sprint(
                sprintId = SprintId(projectId = projectId),
                sprintName = "Sprint 1",
                startDate = LocalDate.of(2000, 1, 1),
                endDate = LocalDate.of(2000, 1, 14),
                sprintBacklogItems = mutableSetOf(),
            )

        // When
        val result = sprint.getSprintBacklogItems(SprintBacklogItemStatus.TO_DO)

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun ensureMoveSprintBacklogItemRightMovesItemFromTodoToInProgress() {
        // Given
        val projectId = UUID.randomUUID()
        val sprintId = UUID.randomUUID()
        val developer =
            Developer(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
                username = "john.doe",
                fullName = FullName(firstName = "John", lastName = "Doe"),
            )
        val sprintBacklogItemId =
            SprintBacklogItemId(projectId = projectId, sprintId = sprintId, productBacklogItemId = UUID.randomUUID())
        val sprintBacklogItem =
            SprintBacklogItem(
                sprintBacklogItemId = sprintBacklogItemId,
                title = "Implement login",
                description = "As a user I want to log in",
                status = SprintBacklogItemStatus.TO_DO,
            )
        val sprint =
            Sprint(
                sprintId = SprintId(projectId = projectId, sprintId = sprintId),
                sprintName = "Sprint 1",
                startDate = LocalDate.of(2000, 1, 1),
                endDate = LocalDate.of(2000, 1, 14),
                sprintBacklogItems = mutableSetOf(sprintBacklogItem),
                status = SprintStatus.IN_PROGRESS,
            )

        // When
        sprint.moveSprintBacklogItem(sprintBacklogItemId, moveDirection = MoveDirection.RIGHT, developer)

        // Then
        assertThat(sprintBacklogItem.status).isEqualTo(SprintBacklogItemStatus.IN_PROGRESS)
        assertThat(sprintBacklogItem.assignedDeveloper).isEqualTo(developer.teamMemberId)
    }

    @Test
    fun ensureMoveSprintBacklogItemRightMovesItemFromInProgressToDone() {
        // Given
        val projectId = UUID.randomUUID()
        val sprintId = UUID.randomUUID()
        val developer =
            Developer(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
                username = "john.doe",
                fullName = FullName(firstName = "John", lastName = "Doe"),
            )
        val sprintBacklogItemId =
            SprintBacklogItemId(projectId = projectId, sprintId = sprintId, productBacklogItemId = UUID.randomUUID())
        val sprintBacklogItem =
            SprintBacklogItem(
                sprintBacklogItemId = sprintBacklogItemId,
                title = "Implement login",
                description = "As a user I want to log in",
                status = SprintBacklogItemStatus.IN_PROGRESS,
            )
        val sprint =
            Sprint(
                sprintId = SprintId(projectId = projectId, sprintId = sprintId),
                sprintName = "Sprint 1",
                startDate = LocalDate.of(2000, 1, 1),
                endDate = LocalDate.of(2000, 1, 14),
                sprintBacklogItems = mutableSetOf(sprintBacklogItem),
                status = SprintStatus.IN_PROGRESS,
            )

        // When
        sprint.moveSprintBacklogItem(sprintBacklogItemId, moveDirection = MoveDirection.RIGHT, developer)

        // Then
        assertThat(sprintBacklogItem.status).isEqualTo(SprintBacklogItemStatus.DONE)
        assertThat(sprintBacklogItem.assignedDeveloper).isEqualTo(developer.teamMemberId)
        assertThat(sprintBacklogItem.sprintBacklogItemMarkedAsDoneEvents).hasSize(1)
    }

    @Test
    fun ensureMoveSprintBacklogItemRightDoesNotMoveItemWhenAlreadyDone() {
        // Given
        val projectId = UUID.randomUUID()
        val sprintId = UUID.randomUUID()
        val developer =
            Developer(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
                username = "john.doe",
                fullName = FullName(firstName = "John", lastName = "Doe"),
            )
        val sprintBacklogItemId =
            SprintBacklogItemId(projectId = projectId, sprintId = sprintId, productBacklogItemId = UUID.randomUUID())
        val sprintBacklogItem =
            SprintBacklogItem(
                sprintBacklogItemId = sprintBacklogItemId,
                title = "Implement login",
                description = "As a user I want to log in",
                status = SprintBacklogItemStatus.DONE,
            )
        val sprint =
            Sprint(
                sprintId = SprintId(projectId = projectId, sprintId = sprintId),
                sprintName = "Sprint 1",
                startDate = LocalDate.of(2000, 1, 1),
                endDate = LocalDate.of(2000, 1, 14),
                sprintBacklogItems = mutableSetOf(sprintBacklogItem),
                status = SprintStatus.IN_PROGRESS,
            )

        // When
        sprint.moveSprintBacklogItem(sprintBacklogItemId, MoveDirection.RIGHT, developer)

        // Then
        assertThat(sprintBacklogItem.status).isEqualTo(SprintBacklogItemStatus.DONE)
    }

    @Test
    fun ensureMoveSprintBacklogItemLeftMovesItemFromDoneToInProgress() {
        // Given
        val projectId = UUID.randomUUID()
        val sprintId = UUID.randomUUID()
        val developer =
            Developer(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
                username = "john.doe",
                fullName = FullName(firstName = "John", lastName = "Doe"),
            )
        val sprintBacklogItemId =
            SprintBacklogItemId(projectId = projectId, sprintId = sprintId, productBacklogItemId = UUID.randomUUID())
        val sprintBacklogItem =
            SprintBacklogItem(
                sprintBacklogItemId = sprintBacklogItemId,
                title = "Implement login",
                description = "As a user I want to log in",
                status = SprintBacklogItemStatus.DONE,
                assignedDeveloper = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
            )
        val sprint =
            Sprint(
                sprintId = SprintId(projectId = projectId, sprintId = sprintId),
                sprintName = "Sprint 1",
                startDate = LocalDate.of(2000, 1, 1),
                endDate = LocalDate.of(2000, 1, 14),
                sprintBacklogItems = mutableSetOf(sprintBacklogItem),
                status = SprintStatus.IN_PROGRESS,
            )

        // When
        sprint.moveSprintBacklogItem(sprintBacklogItemId, MoveDirection.LEFT, developer)

        // Then
        assertThat(sprintBacklogItem.status).isEqualTo(SprintBacklogItemStatus.IN_PROGRESS)
        assertThat(sprintBacklogItem.assignedDeveloper).isEqualTo(developer.teamMemberId)
        assertThat(sprintBacklogItem.sprintBacklogItemUnmarkedAsDoneEvents).hasSize(1)
    }

    @Test
    fun ensureMoveSprintBacklogItemLeftMovesItemFromInProgressToTodo() {
        // Given
        val projectId = UUID.randomUUID()
        val sprintId = UUID.randomUUID()
        val developer =
            Developer(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
                username = "john.doe",
                fullName = FullName(firstName = "John", lastName = "Doe"),
            )
        val sprintBacklogItemId =
            SprintBacklogItemId(projectId = projectId, sprintId = sprintId, productBacklogItemId = UUID.randomUUID())
        val sprintBacklogItem =
            SprintBacklogItem(
                sprintBacklogItemId = sprintBacklogItemId,
                title = "Implement login",
                description = "As a user I want to log in",
                status = SprintBacklogItemStatus.IN_PROGRESS,
                assignedDeveloper = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
            )
        val sprint =
            Sprint(
                sprintId = SprintId(projectId = projectId, sprintId = sprintId),
                sprintName = "Sprint 1",
                startDate = LocalDate.of(2000, 1, 1),
                endDate = LocalDate.of(2000, 1, 14),
                sprintBacklogItems = mutableSetOf(sprintBacklogItem),
                status = SprintStatus.IN_PROGRESS,
            )

        // When
        sprint.moveSprintBacklogItem(sprintBacklogItemId, MoveDirection.LEFT, developer)

        // Then
        assertThat(sprintBacklogItem.status).isEqualTo(SprintBacklogItemStatus.TO_DO)
        assertThat(sprintBacklogItem.assignedDeveloper).isNull()
    }

    @Test
    fun ensureMoveSprintBacklogItemLeftDoesNotMoveItemWhenAlreadyTodo() {
        // Given
        val projectId = UUID.randomUUID()
        val sprintId = UUID.randomUUID()
        val developer =
            Developer(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
                username = "john.doe",
                fullName = FullName(firstName = "John", lastName = "Doe"),
            )
        val sprintBacklogItemId =
            SprintBacklogItemId(projectId = projectId, sprintId = sprintId, productBacklogItemId = UUID.randomUUID())
        val sprintBacklogItem =
            SprintBacklogItem(
                sprintBacklogItemId = sprintBacklogItemId,
                title = "Implement login",
                description = "As a user I want to log in",
                status = SprintBacklogItemStatus.TO_DO,
            )
        val sprint =
            Sprint(
                sprintId = SprintId(projectId = projectId, sprintId = sprintId),
                sprintName = "Sprint 1",
                startDate = LocalDate.of(2000, 1, 1),
                endDate = LocalDate.of(2000, 1, 14),
                sprintBacklogItems = mutableSetOf(sprintBacklogItem),
                status = SprintStatus.IN_PROGRESS,
            )

        // When
        sprint.moveSprintBacklogItem(sprintBacklogItemId, MoveDirection.LEFT, developer)

        // Then
        assertThat(sprintBacklogItem.status).isEqualTo(SprintBacklogItemStatus.TO_DO)
        assertThat(sprintBacklogItem.assignedDeveloper).isNull()
    }

    @Test
    fun ensureMoveSprintBacklogItemFailsWhenDeveloperBelongsToDifferentProject() {
        // Given
        val projectId = UUID.randomUUID()
        val otherProjectId = UUID.randomUUID()
        val sprintId = UUID.randomUUID()
        val developerOfAnotherProject =
            Developer(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = otherProjectId),
                username = "jane.doe",
                fullName = FullName(firstName = "Jane", lastName = "Doe"),
            )
        val sprintBacklogItemId =
            SprintBacklogItemId(projectId = projectId, sprintId = sprintId, productBacklogItemId = UUID.randomUUID())
        val sprintBacklogItem =
            SprintBacklogItem(
                sprintBacklogItemId = sprintBacklogItemId,
                title = "Implement login",
                description = "As a user I want to log in",
                status = SprintBacklogItemStatus.TO_DO,
            )
        val sprint =
            Sprint(
                sprintId = SprintId(projectId = projectId, sprintId = sprintId),
                sprintName = "Sprint 1",
                startDate = LocalDate.of(2000, 1, 1),
                endDate = LocalDate.of(2000, 1, 14),
                sprintBacklogItems = mutableSetOf(sprintBacklogItem),
                status = SprintStatus.IN_PROGRESS,
            )

        // When
        assertThrows<IllegalArgumentException> {
            sprint.moveSprintBacklogItem(sprintBacklogItemId, MoveDirection.RIGHT, developerOfAnotherProject)
        }
    }

    @Test
    fun ensureMoveSprintBacklogItemFailsWhenSprintStatusIsNotInProgress() {
        // Given
        val projectId = UUID.randomUUID()
        val sprintId = UUID.randomUUID()
        val developerOfAnotherProject =
            Developer(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
                username = "jane.doe",
                fullName = FullName(firstName = "Jane", lastName = "Doe"),
            )
        val sprintBacklogItemId =
            SprintBacklogItemId(projectId = projectId, sprintId = sprintId, productBacklogItemId = UUID.randomUUID())
        val sprintBacklogItem =
            SprintBacklogItem(
                sprintBacklogItemId = sprintBacklogItemId,
                title = "Implement login",
                description = "As a user I want to log in",
                status = SprintBacklogItemStatus.TO_DO,
            )
        val sprint =
            Sprint(
                sprintId = SprintId(projectId = projectId, sprintId = sprintId),
                sprintName = "Sprint 1",
                startDate = LocalDate.of(2000, 1, 1),
                endDate = LocalDate.of(2000, 1, 14),
                sprintBacklogItems = mutableSetOf(sprintBacklogItem),
            )

        // When
        assertThrows<IllegalArgumentException> {
            sprint.moveSprintBacklogItem(sprintBacklogItemId, MoveDirection.RIGHT, developerOfAnotherProject)
        }
    }

    @Test
    fun ensureMoveSprintBacklogItemFailsWhenSprintBacklogItemCanNotBeFound() {
        // Given
        val projectId = UUID.randomUUID()
        val sprintId = UUID.randomUUID()
        val developerOfAnotherProject =
            Developer(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
                username = "jane.doe",
                fullName = FullName(firstName = "Jane", lastName = "Doe"),
            )
        val sprintBacklogItemId =
            SprintBacklogItemId(projectId = projectId, sprintId = sprintId, productBacklogItemId = UUID.randomUUID())
        val sprint =
            Sprint(
                sprintId = SprintId(projectId = projectId, sprintId = sprintId),
                sprintName = "Sprint 1",
                startDate = LocalDate.of(2000, 1, 1),
                endDate = LocalDate.of(2000, 1, 14),
                sprintBacklogItems = mutableSetOf(),
                status = SprintStatus.IN_PROGRESS,
            )

        // When
        assertThrows<IllegalArgumentException> {
            sprint.moveSprintBacklogItem(sprintBacklogItemId, MoveDirection.RIGHT, developerOfAnotherProject)
        }
    }
}
