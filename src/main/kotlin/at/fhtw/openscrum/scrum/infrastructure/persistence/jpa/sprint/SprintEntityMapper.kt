package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.sprint

import at.fhtw.openscrum.scrum.domain.model.sprint.Sprint
import at.fhtw.openscrum.scrum.domain.util.Page
import org.springframework.stereotype.Component

@Component
class SprintEntityMapper {
    fun toSprintPage(entityPage: org.springframework.data.domain.Page<SprintEntity>): Page<Sprint> =
        Page(
            content = entityPage.content.map { it.toSprint() }.toMutableList(),
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
