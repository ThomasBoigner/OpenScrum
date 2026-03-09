package at.fhtw.openscrum.management.domain.model.user

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class UserServiceTest {
    lateinit var userService: UserService

    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var encryptionService: EncryptionService

    @BeforeEach
    fun setUp() {
        userService = UserService(encryptionService, userRepository)
    }

    @Test
    fun ensureRegisterUserWorksProperly() {
        // Given
        val username = "JohnDoe"
        val emailAddress = "john.doe@gmail.com"
        val firstName = "John"
        val lastName = "Doe"
        val password = "abc123"
        val role = Role.USER

        whenever(userRepository.existsByEmail(emailAddress)).thenReturn(false)
        whenever(userRepository.existsByUsername(username)).thenReturn(false)
        whenever(encryptionService.hashPassword(password)).thenAnswer { it.arguments[0] }
        whenever(userRepository.save(any())).thenAnswer { it.arguments[0] }

        // When
        val user = userService.registerUser(username, emailAddress, firstName, lastName, password, role)

        // Then
        assertThat(user.username).isEqualTo(username)
        assertThat(user.emailAddress.emailAddress).isEqualTo(emailAddress)
        assertThat(user.fullName.firstName).isEqualTo(firstName)
        assertThat(user.fullName.lastName).isEqualTo(lastName)
        assertThat(user.password).isEqualTo(password)
        assertThat(user.role).isEqualTo(role)
    }

    @Test
    fun ensureRegisterUserThrowsExceptionWhenEmailIsTaken() {
        // Given
        val username = "JohnDoe"
        val emailAddress = "john.doe@gmail.com"
        val firstName = "John"
        val lastName = "Doe"
        val password = "abc123"
        val role = Role.USER

        whenever(userRepository.existsByEmail(emailAddress)).thenReturn(true)

        // When
        assertThrows<IllegalArgumentException> {
            userService.registerUser(
                username,
                emailAddress,
                firstName,
                lastName,
                password,
                role,
            )
        }
    }

    @Test
    fun ensureRegisterUserThrowsExceptionWhenUsernameIsTaken() {
        // Given
        val username = "JohnDoe"
        val emailAddress = "john.doe@gmail.com"
        val firstName = "John"
        val lastName = "Doe"
        val password = "abc123"
        val role = Role.USER

        whenever(userRepository.existsByEmail(emailAddress)).thenReturn(false)
        whenever(userRepository.existsByUsername(username)).thenReturn(true)

        // When
        assertThrows<IllegalArgumentException> {
            userService.registerUser(
                username,
                emailAddress,
                firstName,
                lastName,
                password,
                role,
            )
        }
    }
}
