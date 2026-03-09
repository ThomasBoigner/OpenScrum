package at.fhtw.openscrum.management.infrastructure.configuration

import at.fhtw.openscrum.management.domain.model.user.EncryptionService
import at.fhtw.openscrum.management.domain.model.user.Role
import at.fhtw.openscrum.management.domain.model.user.UserRepository
import at.fhtw.openscrum.management.domain.model.user.UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BeanConfiguration {
    @Bean
    fun userService(
        encryptionService: EncryptionService,
        userRepository: UserRepository,
    ): UserService {
        val userService = UserService(encryptionService, userRepository)
        userService.registerUser(
            "admin",
            "admin@gmail.com",
            "admin",
            "admin",
            "admin",
            Role.MANAGER,
        )
        return userService
    }
}
