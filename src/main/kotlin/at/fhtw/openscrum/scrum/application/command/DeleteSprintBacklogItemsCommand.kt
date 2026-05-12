package at.fhtw.openscrum.scrum.application.command

import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemId
import java.util.UUID

data class DeleteSprintBacklogItemsCommand(
    val projectId: UUID,
    val productBacklogItemId: UUID
)
