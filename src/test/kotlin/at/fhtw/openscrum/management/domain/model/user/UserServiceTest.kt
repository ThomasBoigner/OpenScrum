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

        val authenticatedUser =
            User(
                username = "admin",
                emailAddress = EmailAddress("admin@gmail.com"),
                fullName = FullName("admin", "admin"),
                password = "admin",
                role = Role.MANAGER,
            )

        whenever(userRepository.existsByEmailAddress(emailAddress)).thenReturn(false)
        whenever(userRepository.existsByUsername(username)).thenReturn(false)
        whenever(encryptionService.hashPassword(password)).thenAnswer { it.arguments[0] }
        whenever(userRepository.save(any())).thenAnswer { it.arguments[0] }

        // When
        val user =
            userService.registerUser(
                authenticatedUser,
                username,
                emailAddress,
                firstName,
                lastName,
                password,
            )

        // Then
        assertThat(user.username).isEqualTo(username)
        assertThat(user.emailAddress.emailAddress).isEqualTo(emailAddress)
        assertThat(user.fullName.firstName).isEqualTo(firstName)
        assertThat(user.fullName.lastName).isEqualTo(lastName)
        assertThat(user.password).isEqualTo(password)
        assertThat(user.role).isEqualTo(Role.USER)
    }

    @Test
    fun ensureRegisterUserThrowsExceptionWhenUserIsNotAManager() {
        // Given
        val username = "JohnDoe"
        val emailAddress = "john.doe@gmail.com"
        val firstName = "John"
        val lastName = "Doe"
        val password = "abc123"

        val authenticatedUser =
            User(
                username = "admin",
                emailAddress = EmailAddress("admin@gmail.com"),
                fullName = FullName("admin", "admin"),
                password = "admin",
                role = Role.USER,
            )

        // When
        assertThrows<IllegalArgumentException> {
            userService.registerUser(
                authenticatedUser,
                username,
                emailAddress,
                firstName,
                lastName,
                password,
            )
        }
    }

    @Test
    fun ensureRegisterUserThrowsExceptionWhenEmailIsTaken() {
        // Given
        val username = "JohnDoe"
        val emailAddress = "john.doe@gmail.com"
        val firstName = "John"
        val lastName = "Doe"
        val password = "abc123"

        val authenticatedUser =
            User(
                username = "admin",
                emailAddress = EmailAddress("admin@gmail.com"),
                fullName = FullName("admin", "admin"),
                password = "admin",
                role = Role.MANAGER,
            )

        whenever(userRepository.existsByEmailAddress(emailAddress)).thenReturn(true)

        // When
        assertThrows<IllegalArgumentException> {
            userService.registerUser(
                authenticatedUser,
                username,
                emailAddress,
                firstName,
                lastName,
                password,
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

        val authenticatedUser =
            User(
                username = "admin",
                emailAddress = EmailAddress("admin@gmail.com"),
                fullName = FullName("admin", "admin"),
                password = "admin",
                role = Role.MANAGER,
            )

        whenever(userRepository.existsByEmailAddress(emailAddress)).thenReturn(false)
        whenever(userRepository.existsByUsername(username)).thenReturn(true)

        // When
        assertThrows<IllegalArgumentException> {
            userService.registerUser(
                authenticatedUser,
                username,
                emailAddress,
                firstName,
                lastName,
                password,
            )
        }
    }

    @Test
    fun ensureRegisterUserThrowsExceptionWhenPasswordIsNull() {
        // Given
        val username = "JohnDoe"
        val emailAddress = "john.doe@gmail.com"
        val firstName = "John"
        val lastName = "Doe"
        val password = "abc123"

        val authenticatedUser =
            User(
                username = "admin",
                emailAddress = EmailAddress("admin@gmail.com"),
                fullName = FullName("admin", "admin"),
                password = "admin",
                role = Role.MANAGER,
            )

        whenever(userRepository.existsByEmailAddress(emailAddress)).thenReturn(false)
        whenever(userRepository.existsByUsername(username)).thenReturn(true)

        // When
        assertThrows<IllegalArgumentException> {
            userService.registerUser(
                authenticatedUser,
                username,
                emailAddress,
                firstName,
                lastName,
                password,
            )
        }
    }

    @Test
    fun ensureRegisterAdminReturnsExistingAdminWhenAdminAlreadyExists() {
        // Given
        val existingAdmin =
            User(
                username = "admin",
                emailAddress = EmailAddress("admin@gmail.com"),
                fullName = FullName("admin", "admin"),
                password = "hashedAdmin",
                role = Role.MANAGER,
            )

        whenever(userRepository.findByUsername("admin")).thenReturn(existingAdmin)

        // When
        val result = userService.registerAdmin()

        // Then
        assertThat(result).isEqualTo(existingAdmin)
    }

    @Test
    fun ensureRegisterAdminCreatesAndSavesAdminWhenNoAdminExists() {
        // Given
        whenever(userRepository.findByUsername("admin")).thenReturn(null)
        whenever(encryptionService.hashPassword("admin")).thenAnswer { it.arguments[0] }
        whenever(userRepository.save(any())).thenAnswer { it.arguments[0] }

        // When
        val result = userService.registerAdmin()

        // Then
        assertThat(result.username).isEqualTo("admin")
        assertThat(result.emailAddress.emailAddress).isEqualTo("admin@gmail.com")
        assertThat(result.role).isEqualTo(Role.MANAGER)
    }
}
