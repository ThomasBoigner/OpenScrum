package at.fhtw.openscrum.scrum.domain.model.project

interface ProjectRepository {
    fun save(project: Project): Project
}
