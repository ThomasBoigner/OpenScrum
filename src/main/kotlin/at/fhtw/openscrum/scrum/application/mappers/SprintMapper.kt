package at.fhtw.openscrum.scrum.application.mappers

import at.fhtw.openscrum.scrum.application.dtos.SprintDto
import at.fhtw.openscrum.scrum.domain.model.sprint.Sprint
import at.fhtw.openscrum.scrum.domain.util.Page
import org.springframework.stereotype.Component

@Component
class SprintMapper {
    fun toSprintDtoPage(sprintPage: Page<Sprint>): Page<SprintDto> =
        Page(
            content = sprintPage.content.map { SprintDto(it) }.toMutableList(),
            last = sprintPage.last,
            totalPages = sprintPage.totalPages,
            totalElements = sprintPage.totalElements,
            first = sprintPage.first,
            size = sprintPage.size,
            number = sprintPage.number,
            numberOfElements = sprintPage.numberOfElements,
            empty = sprintPage.empty,
        )
}
