package at.fhtw.openscrum.management.infrastructure.persistence.jpa.user

import at.fhtw.openscrum.management.domain.model.user.EmailAddress
import at.fhtw.openscrum.management.domain.model.user.FullName
import at.fhtw.openscrum.management.domain.model.user.Role
import at.fhtw.openscrum.management.domain.model.user.User
import at.fhtw.openscrum.management.domain.model.user.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("postgres")
class JpaUserRepositoryTest {
    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var userEntityRepository: UserEntityRepository

    @BeforeEach
    fun cleanUp() {
        userEntityRepository.deleteAll()
    }

    @Test
    fun ensureSaveWorksProperly() {
        // Given
        val user =
            User(
                username = "john.doe",
                emailAddress = EmailAddress("john.doe@gmail.com"),
                fullName = FullName("John", "Doe"),
                password = "abc123",
                role = Role.USER,
            )

        // When
        userRepository.save(user)

        // Then
        val savedUser = userRepository.findByUserId(user.userId)
        assertThat(savedUser).isNotNull()
        assertThat(savedUser).isEqualTo(user)
    }

    @Test
    fun ensureExistsByUsernameWorksProperly() {
        // Given
        val user =
            User(
                username = "john.doe",
                emailAddress = EmailAddress("john.doe@gmail.com"),
                fullName = FullName("John", "Doe"),
                password = "abc123",
                role = Role.USER,
            )
        userRepository.save(user)

        // When
        val result = userRepository.existsByUsername(user.username)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun ensureExistsByEmailWorksProperly() {
        // Given
        val user =
            User(
                username = "john.doe",
                emailAddress = EmailAddress("john.doe@gmail.com"),
                fullName = FullName("John", "Doe"),
                password = "abc123",
                role = Role.USER,
            )
        userRepository.save(user)

        // When
        val result = userRepository.existsByEmailAddress(user.emailAddress.emailAddress)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun ensureFindByUsernameWorksProperly() {
        // Given
        val user =
            User(
                username = "john.doe",
                emailAddress = EmailAddress("john.doe@gmail.com"),
                fullName = FullName("John", "Doe"),
                password = "abc123",
                role = Role.USER,
            )

        // When
        userRepository.save(user)

        // Then
        val savedUser = userRepository.findByUsername(user.username)
        assertThat(savedUser).isNotNull()
        assertThat(savedUser).isEqualTo(user)
    }

    @Test
    fun ensureDeleteWorksProperly() {
        // Given
        val user =
            User(
                username = "john.doe",
                emailAddress = EmailAddress("john.doe@gmail.com"),
                fullName = FullName("John", "Doe"),
                password = "abc123",
                role = Role.USER,
            )
        userRepository.save(user)

        // When
        userRepository.delete(user.userId)

        // Then
        assertThat(userRepository.findByUserId(user.userId)).isNull()
    }
}
