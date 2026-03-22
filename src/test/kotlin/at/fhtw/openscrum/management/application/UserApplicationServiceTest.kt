package at.fhtw.openscrum.management.application

import at.fhtw.openscrum.management.application.command.RegisterUserCommand
import at.fhtw.openscrum.management.application.dtos.UserDto
import at.fhtw.openscrum.management.domain.model.user.EmailAddress
import at.fhtw.openscrum.management.domain.model.user.FullName
import at.fhtw.openscrum.management.domain.model.user.Role
import at.fhtw.openscrum.management.domain.model.user.User
import at.fhtw.openscrum.management.domain.model.user.UserRepository
import at.fhtw.openscrum.management.domain.model.user.UserService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class UserApplicationServiceTest {
    lateinit var userApplicationService: UserApplicationService

    @Mock
    lateinit var userService: UserService

    @Mock
    lateinit var userRepository: UserRepository

    @BeforeEach
    fun setUp() {
        userApplicationService = UserApplicationService(userService, userRepository)
    }

    @Test
    fun ensureGetUserByUsernameWorksProperly() {
        // Given
        val user =
            User(
                username = "John.Doe",
                emailAddress = EmailAddress("john.doe@gmail.com"),
                fullName = FullName("John", "Doe"),
                password = "abc123",
                role = Role.USER,
            )

        whenever(userRepository.findByUsername(user.username)).thenReturn(user)

        // When
        val result = userApplicationService.getUserByUsername(user.username)

        // Then
        assertThat(result).isEqualTo(UserDto(user))
    }

    @Test
    fun ensureRegisterUserWorksProperly() {
        // Given
        val username = "John.Doe"
        val emailAddress = "john.doe@gmail.com"
        val firstName = "John"
        val lastName = "Doe"
        val password = "abc123"

        val user =
            User(
                username = username,
                emailAddress = EmailAddress(emailAddress),
                fullName = FullName(firstName, lastName),
                password = password,
                role = Role.USER,
            )

        val authenticatedUser =
            User(
                username = "admin",
                emailAddress = EmailAddress("admin@gmail.com"),
                fullName = FullName("admin", "admin"),
                password = "admin",
                role = Role.MANAGER,
            )

        val command =
            RegisterUserCommand(
                username,
                emailAddress,
                firstName,
                lastName,
                password,
            )

        whenever(userRepository.findByUsername(authenticatedUser.username)).thenReturn(authenticatedUser)

        whenever(
            userService.registerUser(
                authenticatedUser,
                username,
                emailAddress,
                firstName,
                lastName,
                password,
            ),
        ).thenReturn(
            user,
        )

        // When
        val userDto = userApplicationService.registerUser(authenticatedUser.username, command)

        // Then
        assertThat(userDto.username).isEqualTo(username)
        assertThat(userDto.emailAddress).isEqualTo(emailAddress)
        assertThat(userDto.firstName).isEqualTo(firstName)
        assertThat(userDto.lastName).isEqualTo(lastName)
    }

    @Test
    fun ensureRegisterUserThrowsExceptionIfAuthenticatedUserCanNotBeFound() {
        // Given
        val username = "John.Doe"
        val emailAddress = "john.doe@gmail.com"
        val firstName = "John"
        val lastName = "Doe"
        val password = "abc123"

        val user =
            User(
                username = username,
                emailAddress = EmailAddress(emailAddress),
                fullName = FullName(firstName, lastName),
                password = password,
                role = Role.USER,
            )

        val authenticatedUserUsername = "admin"

        val command =
            RegisterUserCommand(
                username,
                emailAddress,
                firstName,
                lastName,
                password,
            )

        whenever(userRepository.findByUsername(authenticatedUserUsername)).thenReturn(null)

        // When
        assertThrows<IllegalArgumentException> {
            userApplicationService.registerUser(
                authenticatedUserUsername,
                command,
            )
        }
    }
}
