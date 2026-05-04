package at.fhtw.openscrum.scrum.domain.model.sprint

import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItem
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemId
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
        val productBacklogItems = mutableSetOf(pbi1, pbi2)

        // When
        sprint.planSprint(scrumMaster, sprintGoal, productBacklogItems)

        // Then
        assertThat(sprint.sprintGoal).isEqualTo(sprintGoal)
        assertThat(sprint.sprintBacklogItems).hasSize(2)
        assertThat(sprint.status).isEqualTo(SprintStatus.IN_PROGRESS)
        assertThat(sprint.sprintBacklogItems.map { it.productBacklogItemId })
            .contains(pbi1.productBacklogItemId, pbi2.productBacklogItemId)
        assertThat(sprint.sprintBacklogItems.map { it.title })
            .contains(pbi1.title, pbi2.title)
        assertThat(sprint.sprintBacklogItems.map { it.description })
            .contains(pbi1.description, pbi2.description)
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
            mutableSetOf(
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
            mutableSetOf(
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
            mutableSetOf(
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
            mutableSetOf(
                ProductBacklogItem(
                    productBacklogItemId = ProductBacklogItemId(projectId = otherProjectId),
                    title = "Implement login",
                    description = "As a user I want to log in",
                ),
            )

        // When / Then
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
            mutableSetOf(
                ProductBacklogItem(
                    productBacklogItemId = ProductBacklogItemId(projectId = projectId),
                    title = "Implement login",
                    description = "As a user I want to log in",
                ),
            )

        // When / Then
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

        // When / Then
        assertThrows<IllegalArgumentException> {
            sprint.planSprint(scrumMaster, sprintGoal, mutableSetOf())
        }
    }
}
