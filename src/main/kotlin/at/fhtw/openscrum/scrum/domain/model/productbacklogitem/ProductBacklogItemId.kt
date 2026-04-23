package at.fhtw.openscrum.scrum.domain.model.productbacklogitem

import java.util.UUID

data class ProductBacklogItemId(
    val projectId: UUID,
    val productBacklogItemId: UUID = UUID.randomUUID(),
)
