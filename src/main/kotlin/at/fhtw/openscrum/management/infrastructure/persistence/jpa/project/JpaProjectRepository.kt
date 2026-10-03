package at.fhtw.openscrum.management.infrastructure.persistence.jpa.project

import at.fhtw.openscrum.management.domain.model.project.Project
import at.fhtw.openscrum.management.domain.model.project.ProjectId
import at.fhtw.openscrum.management.domain.model.project.ProjectRepository
import at.fhtw.openscrum.management.domain.model.user.UserId
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository

@Repository("managementJpaProjectRepository")
class JpaProjectRepository(
    private val projectEntityRepository: ProjectEntityRepository,
) : ProjectRepository {
    override fun findAll(
        nameQuery: String?,
        sortBy: String,
    ): List<Project> =
        projectEntityRepository
            .findAllByProjectNameContainingIgnoreCase(nameQuery ?: "", Sort.by(Sort.Order.asc(sortBy)))
            .map { it.toProject() }

    override fun save(project: Project): Project {
        val projectEntity = ProjectEntity(project)
        projectEntityRepository.save(projectEntity)
        return project
    }

    override fun delete(project: Project) {
        val projectEntity = ProjectEntity(project)
        projectEntityRepository.delete(projectEntity)
    }

    override fun findProjectsOfUser(
        userId: UserId,
        nameQuery: String?,
        sortBy: String,
    ): List<Project> =
        projectEntityRepository
            .findProjectsOfUser(userId.token, nameQuery ?: "", Sort.by(Sort.Order.asc(sortBy)))
            .map { it.toProject() }

    override fun findByProjectId(projectId: ProjectId): Project? = projectEntityRepository.findByProjectId(projectId.token)?.toProject()

    override fun existsByProjectName(projectName: String): Boolean = projectEntityRepository.existsByProjectName(projectName)
}
