package at.fhtw.openscrum.management.application.mappers

import at.fhtw.openscrum.management.application.dtos.UserDto
import at.fhtw.openscrum.management.domain.model.user.User
import at.fhtw.openscrum.management.domain.util.Page
import org.springframework.stereotype.Component

@Component
class UserMapper {
    fun toUserDtoPage(
        userPage: Page<User>,
        toUserDto: (User) -> UserDto,
    ): Page<UserDto> =
        Page(
            content = userPage.content.map(toUserDto).toMutableList(),
            last = userPage.last,
            totalPages = userPage.totalPages,
            totalElements = userPage.totalElements,
            first = userPage.first,
            size = userPage.size,
            number = userPage.number,
            numberOfElements = userPage.numberOfElements,
            empty = userPage.empty,
        )
}
