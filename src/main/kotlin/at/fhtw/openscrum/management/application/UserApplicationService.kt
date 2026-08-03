package at.fhtw.openscrum.management.application

import at.fhtw.openscrum.management.application.command.DeleteUserCommand
import at.fhtw.openscrum.management.application.command.DemoteUserCommand
import at.fhtw.openscrum.management.application.command.PromoteUserCommand
import at.fhtw.openscrum.management.application.command.RegisterUserCommand
import at.fhtw.openscrum.management.application.dtos.UserDto
import at.fhtw.openscrum.management.domain.model.user.UserId
import at.fhtw.openscrum.management.domain.model.user.UserRepository
import at.fhtw.openscrum.management.domain.model.user.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserApplicationService(
    private val userService: UserService,
    private val userRepository: UserRepository,
    private val log: Logger = LoggerFactory.getLogger(UserApplicationService::class.java),
) {
    fun getUsers(authenticatedUserUsername: String): List<UserDto> {
        log.debug("Trying to get all users for user {}", authenticatedUserUsername)

        val authenticatedUser =
            userRepository.findByUsername(authenticatedUserUsername)
                ?: throw IllegalArgumentException("Could not find user with username $authenticatedUserUsername")

        val users = userRepository.findAll()
        log.info("Found all ({}) users", users.size)
        return users.map { UserDto(it, userService.canDeleteUser(authenticatedUser, it)) }
    }

    fun getUserByUsername(username: String): UserDto? {
        log.debug("Trying to get user with username {}", username)
        val user = userRepository.findByUsername(username)
        log.info(user?.let { "Found user $it" } ?: "User with username $username could not be found")
        return user?.let { UserDto(it) }
    }

    @Transactional(readOnly = false)
    fun registerUser(
        authenticatedUserUsername: String,
        command: RegisterUserCommand,
    ): UserDto {
        log.debug("User {} is trying to register user with command: {}", authenticatedUserUsername, command)

        val authenticatedUser =
            userRepository.findByUsername(authenticatedUserUsername)
                ?: throw IllegalArgumentException("Could not find user with username $authenticatedUserUsername")

        return UserDto(
            userService.registerUser(
                authenticatedUser = authenticatedUser,
                username = command.username,
                email = command.email,
                firstName = command.firstName,
                lastName = command.lastName,
                password = command.password,
            ),
        )
    }

    @Transactional(readOnly = false)
    fun promoteUser(
        authenticatedUserUsername: String,
        command: PromoteUserCommand,
    ): UserDto {
        log.debug("User {} is trying to promote user with id {}", authenticatedUserUsername, command.userId)

        val authenticatedUser =
            userRepository.findByUsername(authenticatedUserUsername)
                ?: throw IllegalArgumentException("Could not find user with username $authenticatedUserUsername")

        val user =
            userRepository.findByUserId(UserId(command.userId))
                ?: throw IllegalArgumentException("Could not find user with username $authenticatedUserUsername")

        user.promote(authenticatedUser)
        val promotedUser = userRepository.save(user)
        log.info("Promoted user {}", promotedUser)
        return UserDto(promotedUser, userService.canDeleteUser(authenticatedUser, promotedUser))
    }

    @Transactional(readOnly = false)
    fun demoteUser(
        authenticatedUserUsername: String,
        command: DemoteUserCommand,
    ): UserDto {
        log.debug("User {} is trying to demote user with id {}", authenticatedUserUsername, command.userId)

        val authenticatedUser =
            userRepository.findByUsername(authenticatedUserUsername)
                ?: throw IllegalArgumentException("Could not find user with username $authenticatedUserUsername")

        val user =
            userRepository.findByUserId(UserId(command.userId))
                ?: throw IllegalArgumentException("Could not find user with id ${command.userId}")

        user.demote(authenticatedUser)
        val demotedUser = userRepository.save(user)
        log.info("Demoted user {}", demotedUser)
        return UserDto(demotedUser, userService.canDeleteUser(authenticatedUser, demotedUser))
    }

    @Transactional(readOnly = false)
    fun deleteUser(
        authenticatedUserUsername: String,
        command: DeleteUserCommand,
    ) {
        log.debug("User {} is trying to delete user with id {}", authenticatedUserUsername, command.userId)

        val authenticatedUser =
            userRepository.findByUsername(authenticatedUserUsername)
                ?: throw IllegalArgumentException("Could not find user with username $authenticatedUserUsername")

        val user = userRepository.findByUserId(UserId(command.userId)) ?: return

        userService.deleteUser(authenticatedUser = authenticatedUser, user = user)
    }
}
