package at.fhtw.openscrum.management.domain.model.project

import at.fhtw.openscrum.management.domain.model.user.UserId
import at.fhtw.openscrum.management.domain.util.Page

interface ProjectRepository {
    fun findAll(
        nameQuery: String? = null,
        page: Int = 0,
        size: Int = 5,
        sortBy: String = "projectName",
    ): Page<Project>

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
