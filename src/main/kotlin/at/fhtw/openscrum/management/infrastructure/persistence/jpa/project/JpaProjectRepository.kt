package at.fhtw.openscrum.management.infrastructure.persistence.jpa.project

import at.fhtw.openscrum.management.domain.model.project.Project
import at.fhtw.openscrum.management.domain.model.project.ProjectId
import at.fhtw.openscrum.management.domain.model.project.ProjectRepository
import at.fhtw.openscrum.management.domain.model.user.UserId
import org.springframework.stereotype.Repository

@Repository("managementJpaProjectRepository")
class JpaProjectRepository(
    private val projectEntityRepository: ProjectEntityRepository,
) : ProjectRepository {
    override fun findAll(): List<Project> = projectEntityRepository.findAll().map { it.toProject() }

    override fun save(project: Project): Project {
        val projectEntity = ProjectEntity(project)
        projectEntityRepository.save(projectEntity)
        return project
    }

    override fun findProjectsOfUser(userId: UserId): List<Project> =
        projectEntityRepository.findProjectsOfUser(userId.token).map { it.toProject() }

    override fun findByProjectId(projectId: ProjectId): Project? = projectEntityRepository.findByProjectId(projectId.token)?.toProject()

    override fun existsByProjectName(projectName: String): Boolean = projectEntityRepository.existsByProjectName(projectName)
}
