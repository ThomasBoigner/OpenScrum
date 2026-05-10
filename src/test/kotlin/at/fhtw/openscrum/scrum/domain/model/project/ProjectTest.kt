package at.fhtw.openscrum.scrum.domain.model.project

import at.fhtw.openscrum.scrum.domain.model.teammember.FullName
import at.fhtw.openscrum.scrum.domain.model.teammember.ProductOwner
import at.fhtw.openscrum.scrum.domain.model.teammember.ScrumMaster
import at.fhtw.openscrum.scrum.domain.model.teammember.TeamMemberId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class ProjectTest {
    @Test
    fun ensureDefineSprintLengthWorksProperly() {
        // Given
        val projectId = ProjectId(UUID.randomUUID())
        val scrumMaster =
            ScrumMaster(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId.token),
                username = "username",
                fullName = FullName("First", "Last"),
            )
        val project = Project(projectId = projectId, projectName = "Test Project")

        // When
        project.defineSprintLength(scrumMaster, 3)

        // Then
        assertThat(project.sprintLength.length).isEqualTo(3)
    }

    @Test
    fun ensureDefineSprintLengthThrowsWhenScrumMasterBelongsToAnotherProject() {
        // Given
        val scrumMasterOfAnotherProject =
            ScrumMaster(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = UUID.randomUUID()),
                username = "username",
                fullName = FullName("First", "Last"),
            )
        val project = Project(projectId = ProjectId(UUID.randomUUID()), projectName = "Test Project")

        // When
        assertThrows<IllegalArgumentException> {
            project.defineSprintLength(scrumMasterOfAnotherProject, 0)
        }
    }

    @Test
    fun ensureDefineSprintLengthThrowsWhenSmallerThanOne() {
        // Given
        val projectId = ProjectId(UUID.randomUUID())
        val scrumMaster =
            ScrumMaster(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId.token),
                username = "username",
                fullName = FullName("First", "Last"),
            )
        val project = Project(projectId = projectId, projectName = "Test Project")

        // When + Then
        assertThrows<IllegalArgumentException> {
            project.defineSprintLength(scrumMaster, 0)
        }
    }

    @Test
    fun ensureDefineSprintLengthThrowsWhenBiggerThanFour() {
        // Given
        val projectId = ProjectId(UUID.randomUUID())
        val scrumMaster =
            ScrumMaster(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId.token),
                username = "username",
                fullName = FullName("First", "Last"),
            )
        val project = Project(projectId = projectId, projectName = "Test Project")

        // When + Then
        assertThrows<IllegalArgumentException> {
            project.defineSprintLength(scrumMaster, 5)
        }
    }

    @Test
    fun ensureDefineProductGoalWorksProperly() {
        // Given
        val projectId = ProjectId(UUID.randomUUID())
        val productOwner =
            ProductOwner(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId.token),
                username = "username",
                fullName = FullName("First", "Last"),
            )
        val project = Project(projectId = projectId, projectName = "Test Project")

        // When
        project.defineProductGoal(productOwner, "Deliver MVP")

        // Then
        assertThat(project.productGoal).isEqualTo("Deliver MVP")
    }

    @Test
    fun ensureDefineProductGoalThrowsWhenProductOwnerBelongsToAnotherProject() {
        // Given
        val productOwnerOfAnotherProject =
            ProductOwner(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = UUID.randomUUID()),
                username = "username",
                fullName = FullName("First", "Last"),
            )
        val project = Project(projectId = ProjectId(UUID.randomUUID()), projectName = "Test Project")

        // When + Then
        assertThrows<IllegalArgumentException> {
            project.defineProductGoal(productOwnerOfAnotherProject, "Deliver MVP")
        }
    }

    @Test
    fun ensureDefineProductGoalIsNullWhenBlank() {
        // Given
        val projectId = ProjectId(UUID.randomUUID())
        val productOwner =
            ProductOwner(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId.token),
                username = "username",
                fullName = FullName("First", "Last"),
            )
        val project = Project(projectId = projectId, projectName = "Test Project")

        // When
        project.defineProductGoal(productOwner, "   ")

        // Then
        assertThat(project.productGoal).isNull()
    }

    @Test
    fun ensureDefineDefinitionOfDoneWorksProperly() {
        // Given
        val projectId = ProjectId(UUID.randomUUID())
        val scrumMaster =
            ScrumMaster(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId.token),
                username = "username",
                fullName = FullName("First", "Last"),
            )
        val project = Project(projectId = projectId, projectName = "Test Project")
        val definitionOfDone = "All acceptance criteria are met"

        // When
        project.defineDefinitionOfDone(scrumMaster, definitionOfDone)

        // Then
        assertThat(project.definitionOfDone).isEqualTo(definitionOfDone)
    }

    @Test
    fun ensureDefineDefinitionOfDoneThrowsWhenScrumMasterBelongsToAnotherProject() {
        // Given
        val scrumMasterOfAnotherProject =
            ScrumMaster(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = UUID.randomUUID()),
                username = "username",
                fullName = FullName("First", "Last"),
            )

        val project = Project(projectId = ProjectId(UUID.randomUUID()), projectName = "Test Project")
        val definitionOfDone = "All acceptance criteria are met"

        // When
        assertThrows<IllegalArgumentException> {
            project.defineDefinitionOfDone(
                scrumMasterOfAnotherProject,
                definitionOfDone,
            )
        }
    }

    @Test
    fun ensureDefineDefinitionOfDoneIsNullWhenBlank() {
        // Given
        val projectId = ProjectId(UUID.randomUUID())
        val scrumMaster =
            ScrumMaster(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId.token),
                username = "username",
                fullName = FullName("First", "Last"),
            )
        val project = Project(projectId = projectId, projectName = "Test Project")

        // When
        project.defineDefinitionOfDone(scrumMaster, "   ")

        // Then
        assertThat(project.definitionOfDone).isNull()
    }

    @Test
    fun ensureScheduleSprintWorksProperly() {
        // Given
        val project = Project(projectId = ProjectId(UUID.randomUUID()), projectName = "Test Project", sprintScheduledEvents = mutableListOf())

        // When
        project.scheduleSprint()

        // Then
        assertThat(project.sprintScheduledEvents).hasSize(1)
    }
}
