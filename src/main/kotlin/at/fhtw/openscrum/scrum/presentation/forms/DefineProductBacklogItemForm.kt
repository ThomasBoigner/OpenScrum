package at.fhtw.openscrum.scrum.presentation.forms

import at.fhtw.openscrum.scrum.application.command.DefineProductBacklogItemCommand
import jakarta.validation.constraints.NotBlank
import java.util.UUID

data class DefineProductBacklogItemForm(
    @NotBlank(message = "Project name must not be blank!")
    val title: String? = null,
    @NotBlank(message = "Product name must not be blank!")
    val description: String? = null,
) {
    fun toDefineProductBacklogItemCommand(projectId: UUID): DefineProductBacklogItemCommand =
        DefineProductBacklogItemCommand(
            projectId = projectId,
            title = title!!,
            description = description!!,
        )
}
