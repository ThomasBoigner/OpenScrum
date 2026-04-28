package at.fhtw.openscrum.scrum.domain.model.sprint

interface SprintRepository {
    fun save(sprint: Sprint): Sprint
}
