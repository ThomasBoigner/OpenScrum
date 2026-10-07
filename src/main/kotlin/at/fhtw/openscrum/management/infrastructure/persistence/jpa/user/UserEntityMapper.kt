package at.fhtw.openscrum.management.infrastructure.persistence.jpa.user

import at.fhtw.openscrum.management.domain.model.user.User
import at.fhtw.openscrum.management.domain.util.Page
import org.springframework.stereotype.Component

@Component
class UserEntityMapper {
    fun toUserPage(entityPage: org.springframework.data.domain.Page<UserEntity>): Page<User> =
        Page(
            content = entityPage.content.map { it.toUser() }.toMutableList(),
            last = entityPage.isLast,
            totalPages = entityPage.totalPages,
            totalElements = entityPage.totalElements,
            first = entityPage.isFirst,
            size = entityPage.size,
            number = entityPage.number,
            numberOfElements = entityPage.numberOfElements,
            empty = entityPage.isEmpty,
        )
}
