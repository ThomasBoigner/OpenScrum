package at.fhtw.openscrum.management.infrastructure.persistence.jpa.project

import at.fhtw.openscrum.management.domain.model.project.Project
import at.fhtw.openscrum.management.domain.model.project.ProjectId
import at.fhtw.openscrum.management.domain.model.project.ProjectRepository
import at.fhtw.openscrum.management.domain.model.user.UserId
import at.fhtw.openscrum.management.domain.util.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository

@Repository("managementJpaProjectRepository")
class JpaProjectRepository(
    private val projectEntityRepository: ProjectEntityRepository,
    private val projectEntityMapper: ProjectEntityMapper,
) : ProjectRepository {
    override fun findAll(
        nameQuery: String?,
        page: Int,
        size: Int,
        sortBy: String,
    ): Page<Project> =
        projectEntityMapper.toProjectPage(
            projectEntityRepository
                .findAllByProjectNameContainingIgnoreCase(
                    nameQuery ?: "",
                    PageRequest.of(page, size, Sort.by(Sort.Order.asc(sortBy))),
                ),
        )

    override fun save(project: Project): Project {
        val projectEntity = ProjectEntity(project)
        projectEntityRepository.save(projectEntity)
        return project
    }

    override fun userHasProject(userId: UserId): Boolean {
        TODO("Not yet implemented")
    }

    override fun delete(project: Project) {
        val projectEntity = ProjectEntity(project)
        projectEntityRepository.delete(projectEntity)
    }

    override fun findProjectsOfUser(
        userId: UserId,
        nameQuery: String?,
        page: Int,
        size: Int,
        sortBy: String,
    ): Page<Project> =
        projectEntityMapper.toProjectPage(
            projectEntityRepository
                .findProjectsOfUser(
                    userId.token,
                    nameQuery ?: "",
                    PageRequest.of(page, size, Sort.by(Sort.Order.asc(sortBy))),
                ),
        )

    override fun findByProjectId(projectId: ProjectId): Project? = projectEntityRepository.findByProjectId(projectId.token)?.toProject()

    override fun existsByProjectName(projectName: String): Boolean = projectEntityRepository.existsByProjectName(projectName)
}
