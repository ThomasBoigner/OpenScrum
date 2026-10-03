package at.fhtw.openscrum.management.infrastructure.persistence.jpa.project

import at.fhtw.openscrum.management.domain.model.project.Project
import at.fhtw.openscrum.management.domain.util.Page
import org.springframework.stereotype.Component

@Component
class ProjectEntityMapper {
    fun toProjectPage(entityPage: org.springframework.data.domain.Page<ProjectEntity>): Page<Project> =
        Page(
            content = entityPage.content.map { it.toProject() }.toMutableList(),
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
