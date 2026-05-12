package at.fhtw.openscrum.scrum.domain.model.productbacklogitem

import java.time.LocalDateTime

data class ProductBacklogItemDeleted(
    val productBacklogItemId: ProductBacklogItemId,
    val occurredOn: LocalDateTime = LocalDateTime.now(),
    val eventVersion: Int = 1,
)
