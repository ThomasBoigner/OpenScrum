package at.fhtw.openscrum.management.domain.model.project

import at.fhtw.openscrum.management.domain.model.user.UserId

interface ProjectRepository {
    fun findAll(
        nameQuery: String? = null,
        sortBy: String = "projectName",
    ): List<Project>

    fun findProjectsOfUser(
        userId: UserId,
        nameQuery: String? = null,
        sortBy: String = "project_name",
    ): List<Project>

    fun findByProjectId(projectId: ProjectId): Project?

    fun save(project: Project): Project

    fun delete(project: Project)

    fun existsByProjectName(projectName: String): Boolean
}
