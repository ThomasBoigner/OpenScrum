package at.fhtw.openscrum.management.domain.model.user

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class UserService(
    private val encryptionService: EncryptionService,
    private val userRepository: UserRepository,
    private val log: Logger = LoggerFactory.getLogger(UserService::class.java),
) {
    fun registerUser(
        username: String,
        email: String,
        firstName: String,
        lastName: String,
        password: String,
        role: Role,
    ): User {
        log.debug("Trying to register user {}", username)
        require(!userRepository.existsByEmail(email)) { "User with email $email already exists!" }
        require(!userRepository.existsByUsername(username)) { "User with first name $firstName already exists!" }

        val user =
            User(
                username = username,
                emailAddress = EmailAddress(email),
                fullName = FullName(firstName, lastName),
                password = encryptionService.hashPassword(password),
                role = role,
            )

        log.info("Registered user {}", user)
        return userRepository.save(user)
    }
}
