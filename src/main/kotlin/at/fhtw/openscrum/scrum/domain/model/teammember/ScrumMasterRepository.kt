package at.fhtw.openscrum.scrum.domain.model.teammember

interface ScrumMasterRepository {
    fun save(scrumMaster: ScrumMaster): ScrumMaster
}
