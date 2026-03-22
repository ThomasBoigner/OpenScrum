package at.fhtw.openscrum.management.application

import at.fhtw.openscrum.management.application.command.RegisterUserCommand
import at.fhtw.openscrum.management.domain.model.user.EmailAddress
import at.fhtw.openscrum.management.domain.model.user.FullName
import at.fhtw.openscrum.management.domain.model.user.Role
import at.fhtw.openscrum.management.domain.model.user.User
import at.fhtw.openscrum.management.domain.model.user.UserRepository
import at.fhtw.openscrum.management.domain.model.user.UserService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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

        val command =
            RegisterUserCommand(
                username,
                emailAddress,
                firstName,
                lastName,
                password,
            )

        whenever(
            userService.registerUser(
                username,
                emailAddress,
                firstName,
                lastName,
                password,
                Role.USER,
            ),
        ).thenReturn(
            user,
        )

        // When
        val userDto = userApplicationService.registerUser(command)

        // Then
        assertThat(userDto.username).isEqualTo(username)
        assertThat(userDto.emailAddress).isEqualTo(emailAddress)
        assertThat(userDto.firstName).isEqualTo(firstName)
        assertThat(userDto.lastName).isEqualTo(lastName)
    }
}
