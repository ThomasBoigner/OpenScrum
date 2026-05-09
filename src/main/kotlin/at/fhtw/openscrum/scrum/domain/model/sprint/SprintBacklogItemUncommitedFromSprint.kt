package at.fhtw.openscrum.scrum.domain.model.sprint

import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemId
import java.time.LocalDateTime

data class SprintBacklogItemUncommitedFromSprint(
    val productBacklogItemId: ProductBacklogItemId,
    val occurredOn: LocalDateTime = LocalDateTime.now(),
    val eventVersion: Int = 1,
)