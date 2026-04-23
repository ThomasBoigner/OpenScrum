package at.fhtw.openscrum.scrum.domain.model.productbacklogitem

interface ProductBacklogItemRepository {
    fun save(productBacklogItem: ProductBacklogItem): ProductBacklogItem
}
