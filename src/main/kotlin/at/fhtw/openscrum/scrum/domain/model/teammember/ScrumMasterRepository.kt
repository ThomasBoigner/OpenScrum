package at.fhtw.openscrum.scrum.domain.model.teammember

import java.util.UUID

interface ScrumMasterRepository {
    fun save(scrumMaster: ScrumMaster): ScrumMaster
    fun findByProjectId(projectId: UUID): ScrumMaster?
}
