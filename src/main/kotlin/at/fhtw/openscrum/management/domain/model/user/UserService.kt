package at.fhtw.openscrum.management.domain.model.user

import at.fhtw.openscrum.management.domain.model.project.ProjectRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class UserService(
    private val encryptionService: EncryptionService,
    private val userRepository: UserRepository,
    private val projectRepository: ProjectRepository,
    private val log: Logger = LoggerFactory.getLogger(UserService::class.java),
) {
    fun registerUser(
        authenticatedUser: User,
        username: String,
        email: String,
        firstName: String,
        lastName: String,
        password: String,
    ): User {
        log.debug("Trying to register user {}", username)
        require(authenticatedUser.role.isManager) { "Management permissions are needed!" }
        require(!userRepository.existsByEmailAddress(email)) { "User with email $email already exists!" }
        require(!userRepository.existsByUsername(username)) { "User with username $username already exists!" }

        val hashedPassword =
            encryptionService.hashPassword(password) ?: throw IllegalStateException("Password must not be null!")

        val user =
            User(
                username = username,
                emailAddress = EmailAddress(email),
                fullName = FullName(firstName, lastName),
                password = hashedPassword,
            )

        log.info("Registered user {}", user)
        return userRepository.save(user)
    }

    fun registerAdmin(): User {
        val existingAdmin = userRepository.findByUsername("admin")

        if (existingAdmin != null) {
            return existingAdmin
        }

        val hashedPassword =
            encryptionService.hashPassword("admin") ?: throw IllegalStateException("Password must not be null!")

        val admin =
            User(
                username = "admin",
                emailAddress = EmailAddress("admin@gmail.com"),
                fullName = FullName("admin", "admin"),
                password = hashedPassword,
                role = Role.MANAGER,
            )

        log.info("Registered admin {}", admin)
        return userRepository.save(admin)
    }

    fun deleteUser(
        authenticatedUser: User,
        user: User,
    ) {
        log.debug("Trying to delete user {}", user)
        require(authenticatedUser.role.isManager) { "You have no permission to delete users!" }
        require(authenticatedUser.userId != user.userId) { "You can not delete your own account!" }
        require(projectRepository.findProjectsOfUser(user.userId).isEmpty()) {
            "The user must not be assigned to a project in order to be deleted!"
        }

        userRepository.delete(user.userId)
        log.info("Deleted user {}", user)
    }
}
