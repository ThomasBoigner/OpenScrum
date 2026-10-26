package at.fhtw.openscrum.management.application.mappers

import at.fhtw.openscrum.management.application.dtos.UserDto
import at.fhtw.openscrum.management.domain.model.user.EmailAddress
import at.fhtw.openscrum.management.domain.model.user.FullName
import at.fhtw.openscrum.management.domain.model.user.Role
import at.fhtw.openscrum.management.domain.model.user.User
import at.fhtw.openscrum.management.domain.util.Page
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserMapperTest {
    lateinit var userMapper: UserMapper

    @BeforeEach
    fun setUp() {
        userMapper = UserMapper()
    }

    @Test
    fun ensureToUserDtoPageMapsContent() {
        // Given
        val user1 =
            User(
                username = "john.doe",
                emailAddress = EmailAddress("john.doe@gmail.com"),
                fullName = FullName("John", "Doe"),
                password = "abc123",
                role = Role.USER,
            )

        val user2 =
            User(
                username = "max.mustermann",
                emailAddress = EmailAddress("max.mustermann@gmail.com"),
                fullName = FullName("Max", "Mustermann"),
                password = "abc123",
                role = Role.MANAGER,
            )

        val userPage =
            Page(
                content = mutableListOf(user1, user2),
                last = true,
                totalPages = 1,
                totalElements = 2,
                first = true,
                size = 10,
                number = 0,
                numberOfElements = 2,
                empty = false,
            )

        // When
        val result = userMapper.toUserDtoPage(userPage) { UserDto(it, it == user1) }

        // Then
        assertThat(result.content).hasSize(2)
        assertThat(result.content).containsExactly(
            UserDto(user1, true),
            UserDto(user2, false),
        )
        assertThat(result.last).isEqualTo(userPage.last)
        assertThat(result.totalPages).isEqualTo(userPage.totalPages)
        assertThat(result.totalElements).isEqualTo(userPage.totalElements)
        assertThat(result.first).isEqualTo(userPage.first)
        assertThat(result.size).isEqualTo(userPage.size)
        assertThat(result.number).isEqualTo(userPage.number)
        assertThat(result.numberOfElements).isEqualTo(userPage.numberOfElements)
        assertThat(result.empty).isEqualTo(userPage.empty)
    }
}
