package at.fhtw.openscrum.management.domain.model.project

import at.fhtw.openscrum.management.domain.model.user.UserId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ProjectTest {
    @Test
    fun ensureProjectNameCanNotBeBlank() {
        // Given
        val projectName = ""

        // When
        assertThrows<IllegalArgumentException> {
            Project(
                projectName = projectName,
                developerIds = setOf(UserId()),
                productOwnerId = UserId(),
                scrumMasterId = UserId(),
            )
        }
    }
}
