package at.fhtw.openscrum.scrum.presentation.forms

import at.fhtw.openscrum.scrum.application.command.DefineProductBacklogItemCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.UUID

data class DefineProductBacklogItemForm(
    @NotBlank(message = "Title must not be blank!")
    @Size(max = 255, message = "Title must not be longer than 255 characters!")
    val title: String? = null,
    @NotBlank(message = "Description must not be blank!")
    @Size(max = 4095, message = "Description must not be longer than 4095 characters!")
    val description: String? = null,
) {
    fun toDefineProductBacklogItemCommand(projectId: UUID): DefineProductBacklogItemCommand =
        DefineProductBacklogItemCommand(
            projectId = projectId,
            title = title!!,
            description = description!!,
        )
}
