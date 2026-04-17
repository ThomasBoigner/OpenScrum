package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.teammember

import at.fhtw.openscrum.scrum.domain.model.teammember.ProductOwner
import at.fhtw.openscrum.scrum.domain.model.teammember.ProductOwnerRepository
import org.springframework.stereotype.Repository

@Repository
class JpaProductOwnerRepository(
    private val productOwnerEntityRepository: ProductOwnerEntityRepository,
) : ProductOwnerRepository {
    override fun save(productOwner: ProductOwner): ProductOwner {
        val productOwnerEntity = ProductOwnerEntity(productOwner)
        productOwnerEntityRepository.save(productOwnerEntity)
        return productOwner
    }
}
