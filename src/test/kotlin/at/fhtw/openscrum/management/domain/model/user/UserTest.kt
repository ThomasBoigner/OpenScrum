package at.fhtw.openscrum.management.domain.model.user

import org.assertj.core.api.Assertions.assertThat
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

    @Test
    fun ensurePromoteWorksProperly() {
        // Given
        val authenticatedUser =
            User(
                username = "admin",
                emailAddress = EmailAddress("admin@gmail.com"),
                fullName = FullName("admin", "admin"),
                password = "admin",
                role = Role.MANAGER,
            )

        val user =
            User(
                username = "John.Doe",
                emailAddress = EmailAddress("john.doe@gmail.com"),
                fullName = FullName("John", "Doe"),
                password = "abc123",
                role = Role.USER,
            )

        // When
        user.promote(authenticatedUser)

        // Then
        assertThat(user.role).isEqualTo(Role.MANAGER)
    }

    @Test
    fun ensurePromoteThrowsExceptionWhenAuthenticatedUserIsNotAManager() {
        // Given
        val authenticatedUser =
            User(
                username = "Max.Mustermann",
                emailAddress = EmailAddress("max.mustermann@gmail.com"),
                fullName = FullName("Max", "Mustermann"),
                password = "abc123",
                role = Role.USER,
            )

        val user =
            User(
                username = "John.Doe",
                emailAddress = EmailAddress("john.doe@gmail.com"),
                fullName = FullName("John", "Doe"),
                password = "abc123",
                role = Role.USER,
            )

        // When
        val exception =
            assertThrows<IllegalArgumentException> {
                user.promote(authenticatedUser)
            }

        // Then
        assertThat(exception.message).isEqualTo("You have no permission to promote users!")
        assertThat(user.role).isEqualTo(Role.USER)
    }

    @Test
    fun ensureDemoteWorksProperly() {
        // Given
        val authenticatedUser =
            User(
                username = "admin",
                emailAddress = EmailAddress("admin@gmail.com"),
                fullName = FullName("admin", "admin"),
                password = "admin",
                role = Role.MANAGER,
            )

        val user =
            User(
                username = "John.Doe",
                emailAddress = EmailAddress("john.doe@gmail.com"),
                fullName = FullName("John", "Doe"),
                password = "abc123",
                role = Role.MANAGER,
            )

        // When
        user.demote(authenticatedUser)

        // Then
        assertThat(user.role).isEqualTo(Role.USER)
    }

    @Test
    fun ensureDemoteThrowsExceptionWhenAuthenticatedUserIsNotAManager() {
        // Given
        val authenticatedUser =
            User(
                username = "Max.Mustermann",
                emailAddress = EmailAddress("max.mustermann@gmail.com"),
                fullName = FullName("Max", "Mustermann"),
                password = "abc123",
                role = Role.USER,
            )

        val user =
            User(
                username = "John.Doe",
                emailAddress = EmailAddress("john.doe@gmail.com"),
                fullName = FullName("John", "Doe"),
                password = "abc123",
                role = Role.MANAGER,
            )

        // When
        val exception =
            assertThrows<IllegalArgumentException> {
                user.demote(authenticatedUser)
            }

        // Then
        assertThat(exception.message).isEqualTo("You have no permission to demote users!")
        assertThat(user.role).isEqualTo(Role.MANAGER)
    }

    @Test
    fun ensureDemoteThrowsExceptionForOwnAccount() {
        // Given
        val user =
            User(
                username = "admin",
                emailAddress = EmailAddress("admin@gmail.com"),
                fullName = FullName("admin", "admin"),
                password = "admin",
                role = Role.MANAGER,
            )

        // When
        val exception =
            assertThrows<IllegalArgumentException> {
                user.demote(user)
            }

        // Then
        assertThat(exception.message).isEqualTo("You can not demote your own account!")
        assertThat(user.role).isEqualTo(Role.MANAGER)
    }
}
