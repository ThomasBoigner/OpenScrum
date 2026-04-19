package at.fhtw.openscrum.scrum.domain.model.teammember

import java.util.UUID

interface ProductOwnerRepository {
    fun save(productOwner: ProductOwner): ProductOwner

    fun findByProjectId(projectId: UUID): ProductOwner?
}
