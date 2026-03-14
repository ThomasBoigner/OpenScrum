package at.fhtw.openscrum.management.infrastructure.persistence.jpa.user

import at.fhtw.openscrum.management.domain.model.user.*
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.util.*

@SpringBootTest
@Transactional
@ActiveProfiles("postgres")
class JpaUserRepositoryTest {
    @Autowired
    lateinit var userRepository: UserRepository

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
        val savedUser = userRepository.findByUsername(user.username)
        assertThat(savedUser).isNotNull()
        assertThat(savedUser).isEqualTo(user)
    }
}
