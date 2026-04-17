package at.fhtw.openscrum.scrum.domain.model.teammember

interface ProductOwnerRepository {
    fun save(productOwner: ProductOwner): ProductOwner
}