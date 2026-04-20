package at.fhtw.openscrum.scrum.domain.model.project

import at.fhtw.openscrum.scrum.domain.model.teammember.FullName
import at.fhtw.openscrum.scrum.domain.model.teammember.ScrumMaster
import at.fhtw.openscrum.scrum.domain.model.teammember.TeamMemberId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class ProjectTest {
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
        assertThat(project.definitionOfDone?.definitionOfDone).isEqualTo(definitionOfDone)
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
}
