package at.fhtw.openscrum.management.domain.model.user

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class EmailAddressTest {
    @Test
    fun ensureEmailValidationWorksWithCorrectEmailAddress() {
        // Given
        val emailAddressString = "john.doe@gmail.com"

        // When
        val emailAddress = EmailAddress(emailAddressString)

        // Then
        assertThat(emailAddress.emailAddress).isEqualTo(emailAddressString)
    }

    @Test
    fun ensureEmailValidationThrowsErrorForInvalidEmailAddress() {
        // Given
        val emailAddressString = "john.doe"

        // When
        assertThrows<IllegalArgumentException> { EmailAddress(emailAddressString) }
    }
}
