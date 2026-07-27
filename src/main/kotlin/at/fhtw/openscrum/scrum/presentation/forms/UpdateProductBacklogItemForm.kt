package at.fhtw.openscrum.scrum.presentation.forms

import at.fhtw.openscrum.scrum.application.command.UpdateProductBacklogItemCommand
import jakarta.validation.constraints.NotBlank
import java.util.UUID

data class UpdateProductBacklogItemForm(
    @NotBlank(message = "Title must not be blank!")
    val title: String? = null,
    @NotBlank(message = "Description must not be blank!")
    val description: String? = null,
) {
    fun toUpdateProductBacklogItemCommand(
        projectId: UUID,
        productBacklogItemId: UUID,
    ): UpdateProductBacklogItemCommand =
        UpdateProductBacklogItemCommand(
            projectId = projectId,
            productBacklogItemId = productBacklogItemId,
            title = title!!,
            description = description!!,
        )
}
