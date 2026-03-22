package at.fhtw.openscrum.management.application

import at.fhtw.openscrum.management.application.command.RegisterUserCommand
import at.fhtw.openscrum.management.application.dtos.UserDto
import at.fhtw.openscrum.management.domain.model.user.Role
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
    @Transactional(readOnly = false)
    fun registerUser(command: RegisterUserCommand): UserDto {
        log.debug("Trying to register user with command: {}", command)
        return UserDto(
            userService.registerUser(
                command.username,
                command.email,
                command.firstName,
                command.lastName,
                command.password,
                Role.USER,
            ),
        )
    }
}
