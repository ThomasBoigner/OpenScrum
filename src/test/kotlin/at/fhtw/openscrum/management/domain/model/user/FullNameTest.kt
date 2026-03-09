package at.fhtw.openscrum.management.domain.model.user

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class FullNameTest {
    @Test
    fun ensureFullNameThrowsErrorWhenFirstOrLastNameIsBlank() {
        // Given
        val firstName = ""
        val lastName = ""

        // When
        assertThrows<IllegalArgumentException> { FullName(firstName, lastName) }
    }

    @Test
    fun ensureFullNameThrowsErrorWhenFirstOrLastIsTooLong() {
        // Given
        val firstName = """
            Lorem ipsum dolor sit amet consectetur adipiscing elit. Quisque faucibus ex sapien vitae pellentesque sem placerat.
            In id cursus mi pretium tellus duis convallis. Tempus leo eu aenean sed diam urna tempor.
            Pulvinar vivamus fringilla lacus nec metus bibendum egestas. Iaculis massa nisl malesuada lacinia integer nunc posuere.
            Ut hendrerit semper vel class aptent taciti sociosqu. Ad litora torquent per conubia nostra inceptos himenaeos.
        """

        val lastName = """
            Lorem ipsum dolor sit amet consectetur adipiscing elit. Quisque faucibus ex sapien vitae pellentesque sem placerat.
            In id cursus mi pretium tellus duis convallis. Tempus leo eu aenean sed diam urna tempor.
            Pulvinar vivamus fringilla lacus nec metus bibendum egestas. Iaculis massa nisl malesuada lacinia integer nunc posuere.
            Ut hendrerit semper vel class aptent taciti sociosqu. Ad litora torquent per conubia nostra inceptos himenaeos.
        """

        // When
        assertThrows<IllegalArgumentException> { FullName(firstName, lastName) }
    }
}
