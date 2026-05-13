package at.fhtw.openscrum.management.domain.model.project

import at.fhtw.openscrum.management.domain.model.user.UserId

interface ProjectRepository {
    fun findAll(): List<Project>

    fun findProjectsOfUser(userId: UserId): List<Project>

    fun save(project: Project): Project

    fun existsByProjectName(projectName: String): Boolean
}
