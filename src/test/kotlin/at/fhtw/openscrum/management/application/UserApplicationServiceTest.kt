package at.fhtw.openscrum.management.application

import at.fhtw.openscrum.management.application.command.DeleteUserCommand
import at.fhtw.openscrum.management.application.command.DemoteUserCommand
import at.fhtw.openscrum.management.application.command.PromoteUserCommand
import at.fhtw.openscrum.management.application.command.RegisterUserCommand
import at.fhtw.openscrum.management.application.dtos.RoleDto
import at.fhtw.openscrum.management.application.dtos.UserDto
import at.fhtw.openscrum.management.domain.model.user.EmailAddress
import at.fhtw.openscrum.management.domain.model.user.FullName
import at.fhtw.openscrum.management.domain.model.user.Role
import at.fhtw.openscrum.management.domain.model.user.User
import at.fhtw.openscrum.management.domain.model.user.UserId
import at.fhtw.openscrum.management.domain.model.user.UserRepository
import at.fhtw.openscrum.management.domain.model.user.UserService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
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
    fun ensureGetUsersWithAuthenticatedUserWorksProperly() {
        // Given
        val authenticatedUser =
            User(
                username = "admin",
                emailAddress = EmailAddress("admin@gmail.com"),
                fullName = FullName("admin", "admin"),
                password = "admin",
                role = Role.MANAGER,
            )

        val user1 =
            User(
                username = "John.Doe",
                emailAddress = EmailAddress("john.doe@gmail.com"),
                fullName = FullName("John", "Doe"),
                password = "abc123",
                role = Role.USER,
            )

        val user2 =
            User(
                username = "Max.Mustermann",
                emailAddress = EmailAddress("max.mustermann@gmail.com"),
                fullName = FullName("Max", "Mustermann"),
                password = "abc123",
                role = Role.USER,
            )

        whenever(userRepository.findByUsername(authenticatedUser.username)).thenReturn(authenticatedUser)
        whenever(userRepository.findAll()).thenReturn(listOf(user1, user2))
        whenever(userService.canDeleteUser(authenticatedUser, user1)).thenReturn(true)
        whenever(userService.canDeleteUser(authenticatedUser, user2)).thenReturn(false)

        // When
        val result = userApplicationService.getUsers(authenticatedUser.username)

        // Then
        assertThat(result).isEqualTo(listOf(UserDto(user1, true), UserDto(user2, false)))
    }

    @Test
    fun ensureGetUsersThrowsExceptionIfAuthenticatedUserCanNotBeFound() {
        // Given
        val authenticatedUserUsername = "admin"

        whenever(userRepository.findByUsername(authenticatedUserUsername)).thenReturn(null)

        // When
        assertThrows<IllegalArgumentException> {
            userApplicationService.getUsers(authenticatedUserUsername)
        }
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
    fun ensureGetUserByUsernameReturnsNullWhenUserDoesNotExist() {
        // Given
        val username = "John.Doe"

        whenever(userRepository.findByUsername(username)).thenReturn(null)

        // When
        val result = userApplicationService.getUserByUsername(username)

        // Then
        assertThat(result).isNull()
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

    @Test
    fun ensurePromoteUserWorksProperly() {
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

        val command = PromoteUserCommand(user.userId.token)

        whenever(userRepository.findByUsername(authenticatedUser.username)).thenReturn(authenticatedUser)
        whenever(userRepository.findByUserId(user.userId)).thenReturn(user)
        whenever(userRepository.save(user)).thenReturn(user)
        whenever(userService.canDeleteUser(authenticatedUser, user)).thenReturn(true)

        // When
        val result = userApplicationService.promoteUser(authenticatedUser.username, command)

        // Then
        verify(userRepository).save(user)
        assertThat(result).isEqualTo(UserDto(user, true))
        assertThat(result.role).isEqualTo(RoleDto.MANAGER)
    }

    @Test
    fun ensurePromoteUserThrowsExceptionIfAuthenticatedUserCanNotBeFound() {
        // Given
        val authenticatedUserUsername = "admin"
        val command = PromoteUserCommand(UserId().token)

        whenever(userRepository.findByUsername(authenticatedUserUsername)).thenReturn(null)

        // When
        assertThrows<IllegalArgumentException> {
            userApplicationService.promoteUser(
                authenticatedUserUsername,
                command,
            )
        }
    }

    @Test
    fun ensurePromoteUserThrowsExceptionIfUserCanNotBeFound() {
        // Given
        val authenticatedUser =
            User(
                username = "admin",
                emailAddress = EmailAddress("admin@gmail.com"),
                fullName = FullName("admin", "admin"),
                password = "admin",
                role = Role.MANAGER,
            )

        val userId = UserId()
        val command = PromoteUserCommand(userId.token)

        whenever(userRepository.findByUsername(authenticatedUser.username)).thenReturn(authenticatedUser)
        whenever(userRepository.findByUserId(userId)).thenReturn(null)

        // When
        assertThrows<IllegalArgumentException> {
            userApplicationService.promoteUser(
                authenticatedUser.username,
                command,
            )
        }
    }

    @Test
    fun ensureDemoteUserWorksProperly() {
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

        val command = DemoteUserCommand(user.userId.token)

        whenever(userRepository.findByUsername(authenticatedUser.username)).thenReturn(authenticatedUser)
        whenever(userRepository.findByUserId(user.userId)).thenReturn(user)
        whenever(userRepository.save(user)).thenReturn(user)
        whenever(userService.canDeleteUser(authenticatedUser, user)).thenReturn(true)

        // When
        val result = userApplicationService.demoteUser(authenticatedUser.username, command)

        // Then
        verify(userRepository).save(user)
        assertThat(result).isEqualTo(UserDto(user, true))
        assertThat(result.role).isEqualTo(RoleDto.USER)
    }

    @Test
    fun ensureDemoteUserThrowsExceptionIfAuthenticatedUserCanNotBeFound() {
        // Given
        val authenticatedUserUsername = "admin"
        val command = DemoteUserCommand(UserId().token)

        whenever(userRepository.findByUsername(authenticatedUserUsername)).thenReturn(null)

        // When
        assertThrows<IllegalArgumentException> {
            userApplicationService.demoteUser(
                authenticatedUserUsername,
                command,
            )
        }
    }

    @Test
    fun ensureDemoteUserThrowsExceptionIfUserCanNotBeFound() {
        // Given
        val authenticatedUser =
            User(
                username = "admin",
                emailAddress = EmailAddress("admin@gmail.com"),
                fullName = FullName("admin", "admin"),
                password = "admin",
                role = Role.MANAGER,
            )

        val userId = UserId()
        val command = DemoteUserCommand(userId.token)

        whenever(userRepository.findByUsername(authenticatedUser.username)).thenReturn(authenticatedUser)
        whenever(userRepository.findByUserId(userId)).thenReturn(null)

        // When
        assertThrows<IllegalArgumentException> {
            userApplicationService.demoteUser(
                authenticatedUser.username,
                command,
            )
        }
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
                username = "John.Doe",
                emailAddress = EmailAddress("john.doe@gmail.com"),
                fullName = FullName("John", "Doe"),
                password = "abc123",
                role = Role.USER,
            )

        val command = DeleteUserCommand(user.userId.token)

        whenever(userRepository.findByUsername(authenticatedUser.username)).thenReturn(authenticatedUser)
        whenever(userRepository.findByUserId(user.userId)).thenReturn(user)

        // When
        userApplicationService.deleteUser(authenticatedUser.username, command)

        // Then
        verify(userService).deleteUser(authenticatedUser = authenticatedUser, user = user)
    }

    @Test
    fun ensureDeleteUserThrowsExceptionIfAuthenticatedUserCanNotBeFound() {
        // Given
        val authenticatedUserUsername = "admin"
        val command = DeleteUserCommand(UserId().token)

        whenever(userRepository.findByUsername(authenticatedUserUsername)).thenReturn(null)

        // When
        assertThrows<IllegalArgumentException> {
            userApplicationService.deleteUser(
                authenticatedUserUsername,
                command,
            )
        }
    }

    @Test
    fun ensureDeleteUserReturnsWhenUserCanNotBeFound() {
        // Given
        val authenticatedUser =
            User(
                username = "admin",
                emailAddress = EmailAddress("admin@gmail.com"),
                fullName = FullName("admin", "admin"),
                password = "admin",
                role = Role.MANAGER,
            )

        val userId = UserId()
        val command = DeleteUserCommand(userId.token)

        whenever(userRepository.findByUsername(authenticatedUser.username)).thenReturn(authenticatedUser)
        whenever(userRepository.findByUserId(userId)).thenReturn(null)

        // When
        userApplicationService.deleteUser(authenticatedUser.username, command)

        // Then
        verify(userService, never()).deleteUser(any(), any())
    }
}
