package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.project

import at.fhtw.openscrum.scrum.domain.model.project.Project
import at.fhtw.openscrum.scrum.domain.model.project.ProjectId
import at.fhtw.openscrum.scrum.domain.model.project.ProjectRepository
import org.springframework.stereotype.Repository

@Repository("scrumJpaProjectRepository")
class JpaProjectRepository(
    private val projectEntityRepository: ProjectEntityRepository,
) : ProjectRepository {
    override fun save(project: Project): Project {
        val projectEntity = ProjectEntity(project)
        projectEntityRepository.save(projectEntity)
        return project
    }

    override fun findByProjectId(projectId: ProjectId): Project? {
        TODO("Not yet implemented")
    }
}
