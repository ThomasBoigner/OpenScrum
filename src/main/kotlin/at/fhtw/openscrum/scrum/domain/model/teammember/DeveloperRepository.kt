package at.fhtw.openscrum.scrum.domain.model.teammember

interface DeveloperRepository {
    fun save(developer: Developer): Developer
}