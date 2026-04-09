package at.fhtw.openscrum.management.domain.model.project

interface ProjectRepository {
    fun findAll(): List<Project>

    fun save(project: Project): Project

    fun existsByProjectName(projectName: String): Boolean
}
