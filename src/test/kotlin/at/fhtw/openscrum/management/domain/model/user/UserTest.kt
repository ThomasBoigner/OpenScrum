package at.fhtw.openscrum.management.domain.model.user

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UserTest {
    @Test
    fun ensureUsernameCanNotBeBlank() {
        // Given
        val username = ""

        // When
        assertThrows<IllegalArgumentException> {
            User(
                username = username,
                emailAddress = EmailAddress("john.doe@gmail.com"),
                fullName = FullName("John", "Doe"),
                password = "abc123",
                role = Role.USER,
            )
        }
    }
}
