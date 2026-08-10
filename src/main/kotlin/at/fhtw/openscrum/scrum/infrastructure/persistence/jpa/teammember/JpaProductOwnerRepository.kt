package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.teammember

import at.fhtw.openscrum.scrum.domain.model.teammember.ProductOwner
import at.fhtw.openscrum.scrum.domain.model.teammember.ProductOwnerRepository
import at.fhtw.openscrum.scrum.domain.model.teammember.TeamMemberId
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class JpaProductOwnerRepository(
    private val productOwnerEntityRepository: ProductOwnerEntityRepository,
) : ProductOwnerRepository {
    override fun save(productOwner: ProductOwner): ProductOwner {
        val productOwnerEntity = ProductOwnerEntity(productOwner)
        productOwnerEntityRepository.save(productOwnerEntity)
        return productOwner
    }

    override fun findByProjectId(projectId: UUID): ProductOwner? = productOwnerEntityRepository.findByProjectId(projectId)?.toProductOwner()

    override fun findByProjectIdAndUsername(
        projectId: UUID,
        username: String,
    ): ProductOwner? = productOwnerEntityRepository.findByProjectIdAndUsername(projectId, username)?.toProductOwner()

    override fun delete(teamMemberId: TeamMemberId) =
        productOwnerEntityRepository.deleteByUserIdAndProjectId(teamMemberId.userId, teamMemberId.projectId)
}
