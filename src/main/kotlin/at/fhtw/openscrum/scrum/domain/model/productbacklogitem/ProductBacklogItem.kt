package at.fhtw.openscrum.scrum.domain.model.productbacklogitem

class ProductBacklogItem(
    val id: Long? = null,
    val productBacklogItemId: ProductBacklogItemId,
    title: String,
    description: String,
    status: ProductBacklogItemStatus = ProductBacklogItemStatus.IN_BACKLOG,
) {
    var title: String = ""
        private set(value) {
            require(value.isNotBlank()) { "Title cannot be blank" }
            field = value
        }
    var description: String = ""
        private set(value) {
            require(value.isNotBlank()) { "Description cannot be blank" }
            field = value
        }

    var status: ProductBacklogItemStatus = status
        private set

    init {
        this.title = title
        this.description = description
    }

    fun setStatusToInBacklog() {
        this.status = ProductBacklogItemStatus.IN_BACKLOG
    }

    fun setStatusToCommittedToSprint() {
        this.status = ProductBacklogItemStatus.COMMITTED_TO_SPRINT
    }

    fun setStatusToCommittedToSprintDone() {
        this.status = ProductBacklogItemStatus.COMMITTED_TO_SPRINT_DONE
    }

    override fun toString(): String =
        "ProductBacklogItem(productBacklogItemId=$productBacklogItemId, status=$status, title='$title', description='$description')"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProductBacklogItem

        return productBacklogItemId == other.productBacklogItemId
    }

    override fun hashCode(): Int = productBacklogItemId.hashCode()
}
