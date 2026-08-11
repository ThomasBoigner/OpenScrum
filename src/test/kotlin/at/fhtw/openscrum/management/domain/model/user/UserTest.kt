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
    fun ensurePasswordCanNotBeBlank() {
        // Given
        val password = ""

        // When
        assertThrows<IllegalArgumentException> {
            User(
                username = "John.Doe",
                emailAddress = EmailAddress("john.doe@gmail.com"),
                fullName = FullName("John", "Doe"),
                password = password,
                role = Role.USER,
            )
        }
    }

    @Test
    fun ensureUpdateWorksProperly() {
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
        user.update(
            authenticatedUser = authenticatedUser,
            username = "Jane.Doe",
            emailAddress = EmailAddress("jane.doe@gmail.com"),
            fullName = FullName("Jane", "Doe"),
            password = "def456",
        )

        // Then
        assertThat(user.username).isEqualTo("Jane.Doe")
        assertThat(user.emailAddress).isEqualTo(EmailAddress("jane.doe@gmail.com"))
        assertThat(user.fullName).isEqualTo(FullName("Jane", "Doe"))
        assertThat(user.password).isEqualTo("def456")
        assertThat(user.userInformationChangedEvents).hasSize(1)
        assertThat(user.userInformationChangedEvents.first().userId).isEqualTo(user.userId)
        assertThat(user.userInformationChangedEvents.first().username).isEqualTo("Jane.Doe")
        assertThat(user.userInformationChangedEvents.first().emailAddress).isEqualTo(EmailAddress("jane.doe@gmail.com"))
        assertThat(user.userInformationChangedEvents.first().fullName).isEqualTo(FullName("Jane", "Doe"))
    }

    @Test
    fun ensureUpdateWorksForOwnUser() {
        // Given
        val user =
            User(
                username = "John.Doe",
                emailAddress = EmailAddress("john.doe@gmail.com"),
                fullName = FullName("John", "Doe"),
                password = "abc123",
                role = Role.USER,
            )

        // When
        user.update(
            authenticatedUser = user,
            username = "Jane.Doe",
            emailAddress = EmailAddress("jane.doe@gmail.com"),
            fullName = FullName("Jane", "Doe"),
            password = "def456",
        )

        // Then
        assertThat(user.username).isEqualTo("Jane.Doe")
        assertThat(user.userInformationChangedEvents).hasSize(1)
    }

    @Test
    fun ensureUpdateKeepsPasswordWhenPasswordIsNull() {
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
        user.update(
            authenticatedUser = authenticatedUser,
            username = "Jane.Doe",
            emailAddress = EmailAddress("jane.doe@gmail.com"),
            fullName = FullName("Jane", "Doe"),
            password = null,
        )

        // Then
        assertThat(user.username).isEqualTo("Jane.Doe")
        assertThat(user.emailAddress).isEqualTo(EmailAddress("jane.doe@gmail.com"))
        assertThat(user.fullName).isEqualTo(FullName("Jane", "Doe"))
        assertThat(user.password).isEqualTo("abc123")
        assertThat(user.userInformationChangedEvents).hasSize(1)
    }

    @Test
    fun ensureUpdateThrowsExceptionWhenPasswordIsBlank() {
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
        val exception =
            assertThrows<IllegalArgumentException> {
                user.update(
                    authenticatedUser = authenticatedUser,
                    username = "Jane.Doe",
                    emailAddress = EmailAddress("jane.doe@gmail.com"),
                    fullName = FullName("Jane", "Doe"),
                    password = "",
                )
            }

        // Then
        assertThat(exception.message).isEqualTo("Password must not be blank!")
    }

    @Test
    fun ensureUpdateThrowsExceptionWhenUpdatingAnotherUserWithoutManagerRole() {
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
                user.update(
                    authenticatedUser = authenticatedUser,
                    username = "Jane.Doe",
                    emailAddress = EmailAddress("jane.doe@gmail.com"),
                    fullName = FullName("Jane", "Doe"),
                    password = "def456",
                )
            }

        // Then
        assertThat(exception.message).isEqualTo("You have no permission to update other users!")
        assertThat(user.username).isEqualTo("John.Doe")
        assertThat(user.userInformationChangedEvents).isEmpty()
    }

    @Test
    fun ensureUpdateThrowsExceptionWhenInformationIsBlank() {
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
        assertThrows<IllegalArgumentException> {
            user.update(
                authenticatedUser = authenticatedUser,
                username = "",
                emailAddress = EmailAddress("jane.doe@gmail.com"),
                fullName = FullName("Jane", "Doe"),
                password = "def456",
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
