package at.fhtw.openscrum.management.infrastructure.persistence.jpa.project

import at.fhtw.openscrum.management.domain.model.project.Project
import at.fhtw.openscrum.management.domain.model.project.ProjectRepository
import org.springframework.stereotype.Repository

@Repository
class JpaProjectRepository(
    private val projectEntityRepository: ProjectEntityRepository,
) : ProjectRepository {
    override fun findAll(): List<Project> = projectEntityRepository.findAll().map { it.toProject() }

    override fun save(project: Project): Project {
        val projectEntity = ProjectEntity(project)
        projectEntityRepository.save(projectEntity)
        return project
    }

    override fun existsByProjectName(projectName: String): Boolean = projectEntityRepository.existsByProjectName(projectName)
}
