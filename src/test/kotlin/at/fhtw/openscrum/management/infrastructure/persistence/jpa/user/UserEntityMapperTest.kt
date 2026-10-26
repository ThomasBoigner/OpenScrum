package at.fhtw.openscrum.management.infrastructure.persistence.jpa.user

import at.fhtw.openscrum.management.domain.model.user.Role
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.util.UUID

class UserEntityMapperTest {
    lateinit var userEntityMapper: UserEntityMapper

    @BeforeEach
    fun setUp() {
        userEntityMapper = UserEntityMapper()
    }

    @Test
    fun ensureToUserPageWorksProperly() {
        // Given
        val user1 =
            UserEntity(
                id = 42,
                userId = UUID.randomUUID(),
                username = "john.doe",
                emailAddress = "john.doe@gmail.com",
                fullName = FullNameEmbeddable("John", "Doe"),
                password = "abc123",
                role = Role.USER,
            )

        val user2 =
            UserEntity(
                id = 43,
                userId = UUID.randomUUID(),
                username = "max.mustermann",
                emailAddress = "max.mustermann@gmail.com",
                fullName = FullNameEmbeddable("Max", "Mustermann"),
                password = "abc123",
                role = Role.MANAGER,
            )

        val entityPage = PageImpl(mutableListOf(user1, user2), PageRequest.of(0, 10), 2)

        // When
        val result = userEntityMapper.toUserPage(entityPage)

        // Then
        assertThat(result.content).hasSize(2)
        assertThat(result.content).containsExactly(user1.toUser(), user2.toUser())
        assertThat(result.first).isTrue()
        assertThat(result.last).isTrue()
        assertThat(result.totalElements).isEqualTo(2)
    }
}
