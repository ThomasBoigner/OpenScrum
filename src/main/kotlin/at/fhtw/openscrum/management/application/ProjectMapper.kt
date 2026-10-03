package at.fhtw.openscrum.management.application

import at.fhtw.openscrum.management.application.dtos.ProjectDto
import at.fhtw.openscrum.management.domain.model.project.Project
import at.fhtw.openscrum.management.domain.util.Page
import org.springframework.stereotype.Component

@Component
class ProjectMapper {
    fun toProjectDtoPage(projectPage: Page<Project>): Page<ProjectDto> =
        Page(
            content = projectPage.content.map { ProjectDto(it) }.toMutableList(),
            last = projectPage.last,
            totalPages = projectPage.totalPages,
            totalElements = projectPage.totalElements,
            first = projectPage.first,
            size = projectPage.size,
            number = projectPage.number,
            numberOfElements = projectPage.numberOfElements,
            empty = projectPage.empty,
        )
}
