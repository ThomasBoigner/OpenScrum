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
    fun ensureFindAllWorksProperly() {
        // Given
        val user1 =
            User(
                username = "john.doe",
                emailAddress = EmailAddress("john.doe@gmail.com"),
                fullName = FullName("First", "Last"),
                password = "abc123",
                role = Role.USER,
            )
        val user2 =
            User(
                username = "jane.doe",
                emailAddress = EmailAddress("jane.doe@gmail.com"),
                fullName = FullName("First", "Last"),
                password = "abc123",
                role = Role.USER,
            )
        val user3 =
            User(
                username = "max.mustermann",
                emailAddress = EmailAddress("max.mustermann@gmail.com"),
                fullName = FullName("First", "Last"),
                password = "abc123",
                role = Role.USER,
            )
        userRepository.save(user1)
        userRepository.save(user2)
        userRepository.save(user3)

        // When
        val result = userRepository.findAll("DOE")

        // Then
        assertThat(result).containsExactlyInAnyOrder(user1, user2)
    }

    @Test
    fun ensureFindAlLReturnsEmptyListWhenNothingMatches() {
        // Given
        val user1 =
            User(
                username = "john.doe",
                emailAddress = EmailAddress("john.doe@gmail.com"),
                fullName = FullName("First", "Last"),
                password = "abc123",
                role = Role.USER,
            )
        userRepository.save(user1)

        // When
        val result = userRepository.findAll("abc")

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun ensureFindAllReturnsAllUsersForEmptyQuery() {
        // Given
        val user1 =
            User(
                username = "john.doe",
                emailAddress = EmailAddress("john.doe@gmail.com"),
                fullName = FullName("First", "Last"),
                password = "abc123",
                role = Role.USER,
            )
        val user2 =
            User(
                username = "jane.doe",
                emailAddress = EmailAddress("jane.doe@gmail.com"),
                fullName = FullName("First", "Last"),
                password = "abc123",
                role = Role.USER,
            )
        userRepository.save(user1)
        userRepository.save(user2)

        // When
        val result = userRepository.findAll("")

        // Then
        assertThat(result).containsExactlyInAnyOrder(user1, user2)
    }

    @Test
    fun ensureFindAllReturnsAllUsersForNullQuery() {
        // Given
        val user1 =
            User(
                username = "john.doe",
                emailAddress = EmailAddress("john.doe@gmail.com"),
                fullName = FullName("First", "Last"),
                password = "abc123",
                role = Role.USER,
            )
        val user2 =
            User(
                username = "jane.doe",
                emailAddress = EmailAddress("jane.doe@gmail.com"),
                fullName = FullName("First", "Last"),
                password = "abc123",
                role = Role.USER,
            )
        userRepository.save(user1)
        userRepository.save(user2)

        // When
        val result = userRepository.findAll()

        // Then
        assertThat(result).containsExactlyInAnyOrder(user1, user2)
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
    fun ensureExistsByRoleWorksProperly() {
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
        val resultForUser = userRepository.existsByRole(Role.USER)
        val resultForManager = userRepository.existsByRole(Role.MANAGER)

        // Then
        assertThat(resultForUser).isTrue()
        assertThat(resultForManager).isFalse()
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
