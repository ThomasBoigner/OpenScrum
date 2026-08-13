package at.fhtw.openscrum.scrum.domain.model.teammember

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID

class TeamMemberTest {
    @Test
    fun ensureUpdateInformationWorksProperly() {
        // Given
        val teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = UUID.randomUUID())
        val developer =
            Developer(
                id = 1,
                teamMemberId = teamMemberId,
                username = "jdoe",
                fullName = FullName(firstName = "John", lastName = "Doe"),
            )

        // When
        developer.updateInformation("jane.doe", FullName(firstName = "Jane", lastName = "Doe"))

        // Then
        assertThat(developer.username).isEqualTo("jane.doe")
        assertThat(developer.fullName).isEqualTo(FullName(firstName = "Jane", lastName = "Doe"))
        assertThat(developer.fullName.fullName).isEqualTo("Jane Doe")
        assertThat(developer.id).isEqualTo(1)
        assertThat(developer.teamMemberId).isEqualTo(teamMemberId)
    }
}
