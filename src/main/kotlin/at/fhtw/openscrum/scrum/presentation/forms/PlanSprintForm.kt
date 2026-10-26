package at.fhtw.openscrum.scrum.presentation.forms

import at.fhtw.openscrum.scrum.application.command.PlanSprintCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import java.util.UUID

data class PlanSprintForm(
    @NotBlank(message = "Sprint goal must not be blank!")
    @Size(max = 4095, message = "Sprint goal must not be longer than 4095 characters!")
    val sprintGoal: String? = null,
    @NotEmpty(message = "At least one product backlog item must be selected!")
    val productBacklogIds: Set<UUID> = setOf(),
) {
    fun toDefinePlanSprintCommand(
        projectId: UUID,
        sprintId: UUID,
    ): PlanSprintCommand =
        PlanSprintCommand(
            sprintGoal!!,
            projectId,
            sprintId,
            productBacklogIds,
        )
}
