package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.teammember

import at.fhtw.openscrum.scrum.domain.model.teammember.ScrumMaster
import at.fhtw.openscrum.scrum.domain.model.teammember.ScrumMasterRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class JpaScrumMasterRepository(
    private val scrumMasterEntityRepository: ScrumMasterEntityRepository,
) : ScrumMasterRepository {
    override fun save(scrumMaster: ScrumMaster): ScrumMaster {
        val scrumMasterEntity = ScrumMasterEntity(scrumMaster)
        scrumMasterEntityRepository.save(scrumMasterEntity)
        return scrumMaster
    }

    override fun findByProjectId(projectId: UUID): ScrumMaster? {
        TODO("Not yet implemented")
    }
}
