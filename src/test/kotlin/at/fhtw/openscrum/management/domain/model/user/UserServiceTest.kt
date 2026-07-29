package at.fhtw.openscrum.management.domain.model.user

import at.fhtw.openscrum.management.domain.model.project.Project
import at.fhtw.openscrum.management.domain.model.project.ProjectRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class UserServiceTest {
    lateinit var userService: UserService

    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var encryptionService: EncryptionService

    @Mock
    lateinit var projectRepository: ProjectRepository

    @BeforeEach
    fun setUp() {
        userService = UserService(encryptionService, userRepository, projectRepository)
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

    @Test
    fun ensureDeleteUserWorksProperly() {
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
                username = "JohnDoe",
                emailAddress = EmailAddress("john.doe@gmail.com"),
                fullName = FullName("John", "Doe"),
                password = "abc123",
            )

        whenever(projectRepository.findProjectsOfUser(user.userId)).thenReturn(emptyList())

        // When
        userService.deleteUser(authenticatedUser, user)

        // Then
        verify(userRepository).delete(user.userId)
    }

    @Test
    fun ensureDeleteUserThrowsExceptionWhenUserIsNotAManager() {
        // Given
        val authenticatedUser =
            User(
                username = "JaneDoe",
                emailAddress = EmailAddress("jane.doe@gmail.com"),
                fullName = FullName("Jane", "Doe"),
                password = "abc123",
                role = Role.USER,
            )

        val user =
            User(
                username = "JohnDoe",
                emailAddress = EmailAddress("john.doe@gmail.com"),
                fullName = FullName("John", "Doe"),
                password = "abc123",
            )

        // When
        val exception =
            assertThrows<IllegalArgumentException> {
                userService.deleteUser(authenticatedUser, user)
            }

        // Then
        assertThat(exception.message).isEqualTo("You have no permission to delete users!")
    }

    @Test
    fun ensureDeleteUserThrowsExceptionWhenManagerDeletesOwnAccount() {
        // Given
        val authenticatedUser =
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
                userService.deleteUser(authenticatedUser, authenticatedUser)
            }

        // Then
        assertThat(exception.message).isEqualTo("You can not delete your own account!")
    }

    @Test
    fun ensureDeleteUserThrowsExceptionWhenUserIsAssignedToAProject() {
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
                username = "JohnDoe",
                emailAddress = EmailAddress("john.doe@gmail.com"),
                fullName = FullName("John", "Doe"),
                password = "abc123",
            )

        val project =
            Project(
                projectName = "OpenScrum",
                productOwnerId = user.userId,
                scrumMasterId = authenticatedUser.userId,
            )

        whenever(projectRepository.findProjectsOfUser(user.userId)).thenReturn(listOf(project))

        // When
        val exception =
            assertThrows<IllegalArgumentException> {
                userService.deleteUser(authenticatedUser, user)
            }

        // Then
        assertThat(exception.message).isEqualTo("The user must not be assigned to a project in order to be deleted!")
    }

    @Test
    fun ensureCanDeleteUserReturnsTrueForDeletableUser() {
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
                username = "JohnDoe",
                emailAddress = EmailAddress("john.doe@gmail.com"),
                fullName = FullName("John", "Doe"),
                password = "abc123",
            )

        whenever(projectRepository.findProjectsOfUser(user.userId)).thenReturn(emptyList())

        // When
        val result = userService.canDeleteUser(authenticatedUser, user)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun ensureCanDeleteUserReturnsFalseWhenAuthenticatedUserIsNotAManager() {
        // Given
        val authenticatedUser =
            User(
                username = "JaneDoe",
                emailAddress = EmailAddress("jane.doe@gmail.com"),
                fullName = FullName("Jane", "Doe"),
                password = "abc123",
                role = Role.USER,
            )

        val user =
            User(
                username = "JohnDoe",
                emailAddress = EmailAddress("john.doe@gmail.com"),
                fullName = FullName("John", "Doe"),
                password = "abc123",
            )

        // When
        val result = userService.canDeleteUser(authenticatedUser, user)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun ensureCanDeleteUserReturnsFalseForOwnAccount() {
        // Given
        val authenticatedUser =
            User(
                username = "admin",
                emailAddress = EmailAddress("admin@gmail.com"),
                fullName = FullName("admin", "admin"),
                password = "admin",
                role = Role.MANAGER,
            )

        // When
        val result = userService.canDeleteUser(authenticatedUser, authenticatedUser)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun ensureCanDeleteUserReturnsFalseWhenUserIsAssignedToAProject() {
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
                username = "JohnDoe",
                emailAddress = EmailAddress("john.doe@gmail.com"),
                fullName = FullName("John", "Doe"),
                password = "abc123",
            )

        val project =
            Project(
                projectName = "OpenScrum",
                productOwnerId = user.userId,
                scrumMasterId = authenticatedUser.userId,
            )

        whenever(projectRepository.findProjectsOfUser(user.userId)).thenReturn(listOf(project))

        // When
        val result = userService.canDeleteUser(authenticatedUser, user)

        // Then
        assertThat(result).isFalse()
    }
}
